package com.isel.warpDelivery.model

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.QueryDocumentSnapshot
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.isel.warpDelivery.routeAPI.RouteApi
import org.springframework.stereotype.Component


data class WarperLocation(val username : String, var location : Location, val token : String)




const val MAX_DISTANCE = 15000 //in meters

@Component
class ActiveWarperRepository(val api : RouteApi, val db : Firestore){



    companion object{
        private const val COLLECTION_NAME = "WARPERS"

        //JUST WRAPPER CLASSES SO WE CAN USE document.toObject WITHOUT HAVING NULLABLE PROBLEMS IN OTHER PLACES
        class DummyLocation(var latitude : Double? = null, var longitude : Double? = null){
            fun toLocation() : Location?{
                //TRANSFORM TO VAL
                return if(latitude == null || longitude == null) null else Location(latitude!!,longitude!!)
            }
        }
        class DummyWarperLocation(var username: String? = null, var location : DummyLocation? = null,
                                                                                            val token : String? = null){
            fun toWarper() : WarperLocation?{
                val location = this.location?.toLocation()
                return if(username == null || location == null || token == null) null
                else WarperLocation(username!!, location, token)

            }
        }
    }

    /**
     * Adds a warper to the list of searchable warpers
     * **/
    fun add(warper : WarperLocation){
        val result = db.collection(COLLECTION_NAME).document(warper.username).set(warper).get()
    }

    fun getClosest(location : Location) : WarperLocation?{
        var closestWarperLocation : WarperLocation? = null
        var closestDistance = Double.POSITIVE_INFINITY

        val future = db.collection(COLLECTION_NAME).get()
        val documents: List<QueryDocumentSnapshot> = future.get().documents
        for (document in documents) {
            val warper =  document.toObject(DummyWarperLocation::class.java).toWarper() ?: continue
            val distance = warper.location.getDistance(location)

            if(distance > MAX_DISTANCE) continue;

            val routeDistance : Double = api.getRouteDistance(location, warper.location) ?: continue

            if(routeDistance < closestDistance) {
                closestWarperLocation = warper
                closestDistance = distance
            }
        }

        return closestWarperLocation
    }




    fun updateLocation(username : String, location: Location){
        db.collection(COLLECTION_NAME).document(username)
            .update("location", location);
    }

}
