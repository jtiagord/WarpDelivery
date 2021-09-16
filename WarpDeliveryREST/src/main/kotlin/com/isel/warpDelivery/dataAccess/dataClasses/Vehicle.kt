package com.isel.warpDelivery.dataAccess.dataClasses

import com.isel.warpDelivery.outputmodels.VehicleOutputModel

class Vehicle(val username : String,
              val type : String,
              val registration : String){

    fun toOutputModel() : VehicleOutputModel =
        VehicleOutputModel(type = type ,
        registration = registration)
}
