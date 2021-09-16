package com.isel.warpDelivery.dataAccess.dataClasses

import com.fasterxml.jackson.annotation.JsonIgnore
import com.isel.warpDelivery.outputmodels.WarperOutputModel

class Warper (val username : String,
              val firstname : String,
              val lastname : String,
              val phonenumber : String,
              val email : String,
              @JsonIgnore
              val password :String,
              val balance : Double,
              var vehicles : List<Vehicle> = emptyList()){

    fun toOutputModel(): WarperOutputModel = WarperOutputModel(
        username = username,
        firstname = firstname,
        lastname = lastname,
        phonenumber = phonenumber,
        email = email,
        balance = balance,
        vehicles = vehicles.map{ it.toOutputModel()}
    )
}

data class WarperEdit(
    val firstname: String?,
    val lastname: String?,
    val phonenumber: String?,
    val email: String?,
    var password: String?
)