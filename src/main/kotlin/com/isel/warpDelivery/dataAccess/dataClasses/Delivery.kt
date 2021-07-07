package com.isel.warpDelivery.dataAccess.dataClasses

import com.isel.warpDelivery.inputmodels.Size
import java.sql.Timestamp

class Delivery (val deliveryId : Long?,
                val warperUsername: String?,
                val clientUsername : String?,
                val storeId: Long,
                val state : String,
                val clientPhone: String,
                val purchaseDate : Timestamp?,
                var deliverDate : Timestamp?,
                var rating : Int?,
                val deliverLatitude: Double,
                val deliverLongitude: Double,
                val deliverAddress: String,
                var reward: Float?,
                val type : Size,
                var transitions : List<StateTransition>?)