package com.isel.warpDelivery.inputmodels

data class WarperInputModel(
    val username : String,
    val firstname : String,
    val lastname : String,
    val phonenumber : String,
    val email : String,
    val password :String?,
)

data class VehicleInputModel(
    val type: String,
    val registration: String
)