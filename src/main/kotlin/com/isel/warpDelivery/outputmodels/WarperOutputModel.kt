package com.isel.warpDelivery.outputmodels

import com.isel.warpDelivery.inputmodels.VehicleInputModel

data class WarperOutputModel(
    val username : String,
    val firstname : String,
    val lastname : String,
    val phonenumber : String,
    val email : String,
    val vehicles : List<VehicleOutputModel> = emptyList()
)

data class VehicleOutputModel(
    val type: String,
    val registration: String
)