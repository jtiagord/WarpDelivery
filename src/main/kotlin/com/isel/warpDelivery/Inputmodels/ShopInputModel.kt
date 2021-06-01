package isel.warpDelivery.inputmodels

import isel.warpDelivery.model.Location
import isel.warpDelivery.model.Warper

data class RequestDeliveryInputModel(
    val storeID : Long,
    val storeLocation : Location,
    val deliverySize : String,
    val deliveryLocation : Location,
)

data class RequestActiveWarperInputModel(
    val username : String,
    val currentLocation : Location
)