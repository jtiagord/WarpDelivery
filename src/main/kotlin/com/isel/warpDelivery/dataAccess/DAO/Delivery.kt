package com.isel.warpDelivery.dataAccess.DAO

import com.isel.warpDelivery.model.Location
import java.sql.Timestamp

class Delivery (val deliveryId : Int,
                val warperUsername: String,
                val clientUsername : String,
                val storeId: Long,
                val state : String,
                val clientPhoneNumber: String,
                val purchaseDate : Timestamp,
                var deliveryDate : Timestamp?,
                var rating : Int?,
                val pickupLocationLat: Double,
                val pickupLocationLong: Double,
                val deliverLocationLat: Double,
                val deliverLocationLong: Double,
                val deliveryAddress: String,
                var reward: Float?,
                val type : String,
                val transitions : List<StateTransition>?)