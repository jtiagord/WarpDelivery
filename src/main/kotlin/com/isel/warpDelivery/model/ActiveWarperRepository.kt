package com.isel.warpDelivery.model

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.isel.warpDelivery.inputmodels.Size
import com.isel.warpDelivery.routeAPI.RouteApi
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


data class ActiveWarper(val username : String, val location : Location, val deliverySize: Size, val token : String)




const val MAX_DISTANCE = 15000 //in meters

@Component
class ActiveWarperRepository(val api : RouteApi, val db : Firestore){

    val logger : Logger = LoggerFactory.getLogger(ActiveWarperRepository::class.java)

    companion object{
        private const val COLLECTION_NAME = "WARPERS"

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
                return if(username == null || location == null || token == null || deliverySize ==null) null
                else ActiveWarper(username!!, location, deliverySize,token)

            }
        }
    }

    /**
     * Adds a warper to the list of searchable warpers
     * **/
    fun add(warper : ActiveWarper){
        db.collection(COLLECTION_NAME).document(warper.username).set(warper).get()
    }

    fun getClosest(location : Location,deliverySize: Size) : ActiveWarper?{
        var closestActiveWarper : ActiveWarper? = null
        var closestDistance = Double.POSITIVE_INFINITY

        val future = db.collection(COLLECTION_NAME).whereEqualTo("deliverySize",deliverySize).get()
        val documents = future.get().documents
        for (document in documents) {
            val warper =  document.toObject(DummyWarperLocation::class.java).toWarper() ?: continue
            val distance = warper.location.getDistance(location)
            if(distance > MAX_DISTANCE) continue

            val routeDistance : Double = api.getRouteDistance(location, warper.location) ?: continue

            if(routeDistance < closestDistance) {
                logger
                closestActiveWarper = warper
                closestDistance = distance
            }
        }

        return closestActiveWarper
    }

    fun remove(username : String){
        db.collection(COLLECTION_NAME).document(username)
            .delete()
    }


    fun updateLocation(username : String, location: Location){
        db.collection(COLLECTION_NAME).document(username)
            .update("location", location)
    }

}
