package utils

import kotlinx.coroutines.*
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient


data class ActiveWarper(val username : String, val location : Location, val deliverySize: Size, val token : String)

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
        private const val ACTIVE_DELIVERIES = "ACTIVEDELIVERIES"

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

    /**
     * Adds a warper to the list of searchable warpers
     * **/
    fun add(warper : ActiveWarper){
        db.collection(ACTIVE_WARPERS).document(warper.username).set(warper).get()
    }



    fun getClosest(location : Location,deliverySize: Size) : ActiveWarper? {
        var closestActiveWarper: ActiveWarper? = null
        var closestDistance = Double.POSITIVE_INFINITY


        db.runTransaction {
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
        }}.get()

        if(closestActiveWarper != null) {
            db.collection(ACTIVE_WARPERS).document(closestActiveWarper!!.username).delete().get()
            db.collection(ACTIVE_DELIVERIES).document(closestActiveWarper!!.username).create(closestActiveWarper!!).get()
        }


        //if(closestActiveWarper != null ) remove(closestActiveWarper.username)
        return closestActiveWarper
    }



    fun remove(username : String){
        db.collection(ACTIVE_WARPERS).document(username)
            .delete()
    }


    fun updateLocation(username : String, location: Location){
        db.collection(ACTIVE_WARPERS).document(username)
            .update("location", location)
    }

}
