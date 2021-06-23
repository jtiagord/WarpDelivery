package com.isel.warpDelivery.inputmodels

import com.isel.warpDelivery.model.Location


data class RequestDeliveryInputModel(
    val storeID : Long,
    val storeLocation : Location,
    val deliverySize : String,
    val deliveryLocation : Location,
)

data class RequestActiveWarperInputModel(
    val username : String,
    val location : Location,
    val messageToken: String
)