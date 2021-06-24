package com.isel.warpDelivery.inputmodels

import com.isel.warpDelivery.dataAccess.DAO.Address

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

fun AddressInputModel.toAddress(username: String) = Address(username, this.postalCode, this.address)

data class RatingAndRewardInputModel (
    val rating: Int,
    val reward: Float
)