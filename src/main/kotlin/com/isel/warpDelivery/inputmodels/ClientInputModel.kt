package com.isel.warpDelivery.inputmodels

data class ClientInputModel(
    val username : String,
    val firstName: String,
    val lastName : String,
    val phoneNumber : String,
    val password : String,
    val email : String
)

data class AddressInputModel (
    val postalCode: String,
    val address : String
)

data class RatingAndRewardInputModel (
    val rating: Int,
    val reward: Float
)