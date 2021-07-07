package com.isel.warpDelivery.inputmodels

import com.isel.warpDelivery.dataAccess.dataClasses.Address

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
    val address : String,
    val latitude: Double,
    val longitude: Double
)

fun AddressInputModel.toAddress(username: String) = Address(
    clientUsername = username,
    postalCode = postalCode,
    address= address,
    latitude = latitude,
    longitude =longitude
)

data class RatingAndRewardInputModel (
    val rating: Int,
    val reward: Float
)