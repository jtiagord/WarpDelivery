package com.isel.warpDelivery.dataAccess.DAO

import java.sql.Timestamp

class Delivery (val deliveryId : Int,
                val clientUsername : String,
                var warperUsername: String?,
                val state : String,
                var storeId: Long,
                var clientPhoneNumber: String,
                val purchaseDate : Timestamp,
                val deliveryDate : Timestamp?,
                val rating : Int?,
                val price : Float,
                val reward: Float?,
                val type : String,
                var transitions : List<StateTransition>?)