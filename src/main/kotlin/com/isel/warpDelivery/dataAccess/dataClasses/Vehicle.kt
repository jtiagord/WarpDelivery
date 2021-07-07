package com.isel.warpDelivery.dataAccess.dataClasses

import com.isel.warpDelivery.outputmodels.VehicleOutputModel

class Vehicle(val username : String,
                val vehicleType : String,
                val vehicleRegistration : String){

    fun toOutputModel() : VehicleOutputModel =
        VehicleOutputModel(type = vehicleType ,
        registration = vehicleRegistration)
}
