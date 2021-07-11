package com.isel.warpDelivery.dataAccess.dataClasses

data class Store (
    val storeId : String?,
    val name : String,
    val postalcode : String,
    val address : String,
    val latitude : Double,
    val longitude : Double,
    var ApiKey: String
)