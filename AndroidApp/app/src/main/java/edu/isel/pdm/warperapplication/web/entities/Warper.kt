package edu.isel.pdm.warperapplication.web.entities

data class Warper (
    val username : String?,
    val firstname : String?,
    val lastname : String?,
    val phonenumber : String?,
    val email : String?,
    val vehicles : List<Vehicle>?
)