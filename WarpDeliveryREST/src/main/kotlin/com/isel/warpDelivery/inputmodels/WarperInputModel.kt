package com.isel.warpDelivery.inputmodels

import com.isel.warpDelivery.dataAccess.dataClasses.Vehicle
import com.isel.warpDelivery.dataAccess.dataClasses.Warper

data class WarperInputModel(
    val username : String,
    val firstname : String,
    val lastname : String,
    val phonenumber : String,
    val email : String,
    var password :String,
    val vehicles : List<VehicleInputModel> = emptyList()
)

data class WarperLoginInputModel(
    val username: String,
    val password: String
)

data class VehicleInputModel(
    val type: String,
    val registration: String
)

fun WarperInputModel.toDao(): Warper {
    val vehiclesDao = vehicles.map{ it.toDao(username) }
    return Warper(username = username, firstname = firstname, lastname = lastname, phonenumber = phonenumber,
        email = email, password=password,0.0,vehicles = vehiclesDao
    )
}
fun VehicleInputModel.toDao(username: String)  : Vehicle = Vehicle(registration = this.registration,
    type =  type, username = username)

