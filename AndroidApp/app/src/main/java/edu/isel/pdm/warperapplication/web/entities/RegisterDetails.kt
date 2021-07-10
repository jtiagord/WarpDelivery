package edu.isel.pdm.warperapplication.web.entities

data class RegisterDetails(
    val username: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val phonenumber: String
)