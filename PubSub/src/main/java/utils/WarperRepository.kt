package utils

import ActiveWarper
import DeliveringWarper
import Delivery
import Location
import com.google.cloud.Timestamp
import kotlinx.coroutines.*
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.SetOptions
import com.google.firebase.cloud.FirestoreClient
import java.util.*
import kotlin.collections.ArrayList


enum class Size(val text: String) {
    SMALL("small"), MEDIUM("medium"), LARGE("large");

    companion object {
        fun fromText(text: String): Size?{
            for (value in values()) {
                if (value.text.equals(text,ignoreCase = true)) {
                    return value
                }
            }
            return null
        }
    }
}


const val MAX_DISTANCE = 15000 //in meters
class ActiveWarperRepository{
    private val api: RouteApi = RouteApi()
    private val db : Firestore  = FirestoreClient.getFirestore()
    companion object{
        private const val ACTIVE_WARPERS = "WARPERS"
        private const val DELIVERING_WARPERS = "DELIVERINGWARPERS"
        private const val PENDING_DELIVERIES = "PENDING_DELIVERIES"

        //JUST WRAPPER CLASSES SO WE CAN USE document.toObject WITHOUT HAVING NULLABLE PROBLEMS IN OTHER PLACES
        class DummyLocation(var latitude : Double? = null , var longitude : Double? = null){
            fun toLocation() : Location?{
                //TRANSFORM TO VAL
                return if(latitude == null || longitude == null) null else Location(latitude!!,longitude!!)
            }
        }
        class DummyWarperLocation(var username: String? = null, var location : DummyLocation? = null,
                                  val deliverySize: Size? = null, val token : String? = null){
            fun toWarper() : ActiveWarper?{
                val location = this.location?.toLocation()
                return if(username == null || location == null || token == null || deliverySize ==null ) null
                else ActiveWarper(username!!, location, deliverySize,token)

            }
        }
    }




    fun getClosest(location : Location,deliverySize: Size) : ActiveWarper? {
        var closestActiveWarper: ActiveWarper? = null
        var closestDistance = Double.POSITIVE_INFINITY

        val queryRef = db.collection(ACTIVE_WARPERS)
        val future = queryRef.get()
        val documents = future.get().documents
        val distanceList : List<Pair<ActiveWarper,Double?>>

        //Make every request to the routing api asynchronously and wait for it
        runBlocking {
            val list = ArrayList<Deferred<Pair<ActiveWarper,Double?>>>()
            for (document in documents) {
                val warper = document.toObject(DummyWarperLocation::class.java).toWarper() ?: continue

                val distance = warper.location.getDistance(location)

                if (distance > MAX_DISTANCE) continue

                list.add(async { Pair(warper,api.getRouteDistance(location, warper.location)) })
            }
            distanceList = list.awaitAll()
        }

        for(routeDistance in distanceList){
            if(routeDistance.second?:continue < closestDistance) {
                closestActiveWarper = routeDistance.first
                closestDistance = routeDistance.second!!
            }
        }

        return closestActiveWarper
    }

    fun setWarperForDelivery(warper : ActiveWarper, delivery : Delivery){
        val deliveringWarper = DeliveringWarper(warper, delivery, DeliveringWarperState.RETRIEVING)
        db.collection(ACTIVE_WARPERS).document(warper.username).delete().get()
        db.collection(DELIVERING_WARPERS).document(warper.username).create(deliveringWarper).get()
    }

    fun setPendingDelivery(delivery : Delivery){
        val batch = db.batch()
        db.collection(PENDING_DELIVERIES).document(delivery.id).set(delivery)
        db.collection(PENDING_DELIVERIES).document(delivery.id).update("timestamp",Timestamp.now())
        batch.commit()
    }

    fun remove(username : String){
        db.collection(ACTIVE_WARPERS).document(username)
            .delete()
    }
}
