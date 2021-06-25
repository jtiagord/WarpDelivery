package com.isel.warpDelivery.dataAccess.DAO

import com.isel.warpDelivery.model.Location
import java.sql.Timestamp

class Delivery (val deliveryId : Long,
                val warperUsername: String,
                val clientUsername : String?,
                val storeId: Long,
                val state : String,
                val clientPhone: String,
                val purchaseDate : Timestamp,
                var deliveryDate : Timestamp?,
                var rating : Int?,
                val pickupLatitude: Double,
                val pickupLongitude: Double,
                val deliverLatitude: Double,
                val deliverLongitude: Double,
                val deliveryAddress: String,
                var reward: Float?,
                val type : String,
                var transitions : List<StateTransition>?)