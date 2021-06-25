package com.isel.warpDelivery.dataAccess.DAO

import com.isel.warpDelivery.outputmodels.WarperOutputModel

class Warper (val username : String,
              val firstname : String,
              val lastname : String,
              val phonenumber : String,
              val email : String,
              val password :String?,
              var vehicles : List<Vehicle> = emptyList()){

    fun toOutputModel(): WarperOutputModel = WarperOutputModel(
        username = username,
        firstname = firstname,
        lastname = lastname,
        phonenumber = phonenumber,
        email = email,
        vehicles = vehicles.map{ it.toOutputModel()}
    )
}