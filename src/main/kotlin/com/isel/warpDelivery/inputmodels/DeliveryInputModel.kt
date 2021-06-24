package com.isel.warpDelivery.inputmodels

import com.isel.warpDelivery.model.Location
import java.sql.Timestamp

data class DeliveryInputModel(
    val storeId : Long,
    val type : String,
    val clientUsername: String,
    val clientPhoneNumber: String,
    val purchaseDate: Timestamp,
    val price: Float,
)