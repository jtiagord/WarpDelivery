package edu.isel.pdm.warperapplication.web.entities

data class LoginToken (
    val token: String
)

data class TokenPayload (
    val userType: String,
    val id: String,
    val exp: Long,
    val iat: Long
)
