package com.isel.warpDelivery.inputmodels

import com.isel.warpDelivery.model.Location


data class RequestDeliveryInputModel(
    val storeId : Long,
    val userPhone : String,
    val deliverySize : String,
    val deliveryLocation : Location,
)

data class RequestActiveWarperInputModel(
    val username : String,
    val location : Location,
    val messageToken: String
)