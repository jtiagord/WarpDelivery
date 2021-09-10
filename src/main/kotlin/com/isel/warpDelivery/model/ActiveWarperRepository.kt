package com.isel.warpDelivery.model

import com.google.cloud.firestore.Firestore
import com.isel.warpDelivery.errorHandling.ApiException
import com.isel.warpDelivery.inputmodels.Size
import com.isel.warpDelivery.routeAPI.RouteApi
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.sql.Timestamp

enum class DeliveringWarperState {
    RETRIEVING, DELIVERING
}

data class ActiveWarper(val username : String, val location : Location, val deliverySize: Size, val token : String?)

data class ActiveDelivery(val id : String, val size : Size, val pickUpLocation : Location, val deliveryLocation : Location)

class DeliveringWarper(val username : String, val location : Location,
                       val delivery : ActiveDelivery, val state : DeliveringWarperState){
    constructor(warper : ActiveWarper , delivery : ActiveDelivery, state : DeliveringWarperState)
            : this(warper.username, warper.location,delivery, state)

}


const val MAX_DISTANCE = 15000 //in meters

@Component
class ActiveWarperRepository(val api : RouteApi, val db : Firestore){

    val logger : Logger = LoggerFactory.getLogger(ActiveWarperRepository::class.java)

    companion object{
        private const val DELIVERING_WARPERS = "DELIVERINGWARPERS"
        private const val PENDING_DELIVERIES = "PENDING_DELIVERIES"
        private const val ACTIVE_WARPERS = "WARPERS"



        /**
         * JUST WRAPPER CLASSES SO WE CAN USE document.toObject FROM FIRESTORE
         * WITHOUT HAVING NULLABLE PROBLEMS IN OTHER PLACES
         * **/
        class DummyLocation(var latitude : Double? = null , var longitude : Double? = null){
            fun toLocation() : Location?{
                //TRANSFORM TO VAL
                return if(latitude == null || longitude == null) null else Location(latitude!!,longitude!!)
            }
        }

        class DummyDelivery(val id : String? = null, val size : Size? = null,
                            val pickUpLocation : DummyLocation? = null, val deliveryLocation : DummyLocation? = null,
                            val timestamp : Timestamp? = null) {

            fun toDelivery() : ActiveDelivery? {
                val pickUpLocation = this.pickUpLocation?.toLocation()
                val deliveryLocation = this.deliveryLocation?.toLocation()

                return if (id == null || size == null || pickUpLocation == null || deliveryLocation == null) null
                else ActiveDelivery(id, size, pickUpLocation, deliveryLocation)
            }

        }

        class DummyWarperLocation(var username: String? = null, var location : DummyLocation? = null,
                                  val deliverySize: Size? = null, val token : String? = null){
            fun toWarper() : ActiveWarper?{
                val location = this.location?.toLocation()
                return if(username == null || location == null || deliverySize ==null) null
                else ActiveWarper(username!!, location, deliverySize,token)

            }
        }

        class DummyDeliveringWarper(val username : String?=null, val location : DummyLocation? = null,
                                    val deliveryId : String? = null, val delivery : DummyDelivery? = null,
                                    val state : DeliveringWarperState? = null) {

            fun toDeliveringWarper(): DeliveringWarper? {
                val location = this.location?.toLocation()
                val delivery = this.delivery?.toDelivery()
                return if (username == null || location == null || delivery == null || state == null)
                    null
                else DeliveringWarper(username, location, delivery,state)
            }
        }
    }

    /**
     * Adds a warper to the list of searchable warpers/
     * if there is a pending delivery in range it will assign the oldest one
     * **/
    fun add(warper : ActiveWarper){

        val queryRef = db.collection(PENDING_DELIVERIES).whereEqualTo("size",warper.deliverySize)
            .orderBy("timestamp")
        val warperRef = db.collection(ACTIVE_WARPERS).document(warper.username)

        val warperDeliveryRef = db.collection(DELIVERING_WARPERS).document(warper.username)

        if(warperDeliveryRef.get().get().exists())
            throw ApiException("The warper ${warper.username} is already delivering", HttpStatus.BAD_REQUEST)


            db.runTransaction { transaction ->
                val docs = transaction.get(queryRef).get().documents
                for (document in docs) {
                    val deliverydoc = document.data
                    val deliveryobj = document.toObject(DummyDelivery::class.java)
                    val delivery = deliveryobj.toDelivery() ?: continue
                    if (delivery.pickUpLocation.getDistance(warper.location) <= MAX_DISTANCE) {
                        transaction.delete(document.reference)
                        transaction.delete(warperRef)
                        transaction.create(warperDeliveryRef, DeliveringWarper(warper, delivery,
                            DeliveringWarperState.RETRIEVING))
                        return@runTransaction
                    }
                }

                transaction.set(warperRef, warper)
            }

    }

    /**
     * Gets the closest warper to the location
     */
    fun getClosest(location : Location,deliverySize: Size) : ActiveWarper? {
        var closestActiveWarper: ActiveWarper? = null
        var closestDistance = Double.POSITIVE_INFINITY

        val queryRef = db.collection(ACTIVE_WARPERS)
        val future = queryRef.get()
        val documents = future.get().documents
        val distanceList : List<Pair<ActiveWarper,Double?>>

        //Make every request to the routing api asynchronously and wait for it
        runBlocking {
            val list = ArrayList<Deferred<Pair<ActiveWarper, Double?>>>()
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

    /**
     * Changes a warper from looking for delivery to already delivering
     */
    private fun setWarperForDelivery(warper : ActiveWarper, activeDelivery : ActiveDelivery){
        val deliveringWarper = DeliveringWarper(warper, activeDelivery,
            DeliveringWarperState.RETRIEVING)
        db.collection(ACTIVE_WARPERS).document(warper.username).delete().get()
        db.collection(DELIVERING_WARPERS).document(warper.username).create(deliveringWarper).get()
    }

    /**
     * Removes a warper from delivering
     */
    fun removeDeliveringWarper(warperName : String) : DeliveringWarper?{
        val deliveringWarperRef = db.collection(DELIVERING_WARPERS).document(warperName)

        val deliveringWarper = deliveringWarperRef.get().get()
            deliveringWarperRef.delete()

        return if(deliveringWarper.exists())
            deliveringWarper.toObject(DummyDeliveringWarper::class.java)?.toDeliveringWarper()
        else null
    }

    fun getWarperWithDeliveryId(deliveryId : String) : DeliveringWarper?{
        val deliveringWarperRef = db.collection(DELIVERING_WARPERS).whereEqualTo("delivery.id", deliveryId)
        val documents = deliveringWarperRef.get().get().documents
        logger.info("Documents.size : ${documents.size}")

        if(documents.size >= 1)
            logger.info("Document : ${documents[0]}")
        return if(documents.size >= 1)
            documents[0].toObject(DummyDeliveringWarper::class.java).toDeliveringWarper()
        else null
    }




    fun remove(username : String){
        db.collection(ACTIVE_WARPERS).document(username)
            .delete()
    }

    fun cancelDelivery(deliveryId: String){
        val deliveryRef = db.collection(PENDING_DELIVERIES).document(deliveryId)
        val warperQuery = db.collection(DELIVERING_WARPERS).whereEqualTo("deliveryId", deliveryId)

        db.runTransaction {  tr ->

            val warperQuery2 = tr.get(warperQuery)
            tr.delete(deliveryRef)
            for(document in warperQuery2.get().documents){
                tr.delete(document.reference)
            }
        }

    }


    fun updateLocation(username : String, location: Location){
        db.collection(ACTIVE_WARPERS).document(username)
            .update("location", location)
        db.collection(DELIVERING_WARPERS).document(username)
            .update("location", location)
    }

    fun updateState(username: String, state: DeliveringWarperState) {
        db.collection(DELIVERING_WARPERS).document(username).update("state", state)
    }

}
