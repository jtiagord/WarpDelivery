package com.isel.warpDelivery.inputmodels

import com.isel.warpDelivery.dataAccess.dataClasses.Delivery
import com.isel.warpDelivery.dataAccess.dataClasses.Store
import com.isel.warpDelivery.model.Location


data class RequestDeliveryInputModel(
    val userPhone : String,
    val deliverySize : Size,
    val address : String,
    val deliveryLocation : Location
) {
    fun toDelivery(warperUsername: String?, clientUsername: String?,storeId : String): Delivery {
        return Delivery(null, warperUsername, clientUsername, storeId, "Looking for Warper", userPhone, null,
        null, null, deliveryLocation.latitude, deliveryLocation.longitude, address, null, deliverySize, emptyList())
    }
}

enum class Size(val text: String) {
    SMALL("small"), MEDIUM("medium"), LARGE("large");

    companion object {
        fun fromText(text: String): Size?{
            for (value in values()) {
                if (value.text.equals(text,ignoreCase = true)) {
                    return value
                }
            }
            return null
        }
    }
}

data class ActiveWarperInputModel(
    val vehicle : String,
    val location : Location,
    val notificationToken: String
)

data class StoreInputModel (
    val name : String,
    val postalcode : String,
    val address : String,
    val location:  Location
)

fun StoreInputModel.toDao(ApiKey : String): Store =
    Store(null,name,postalcode,address,location.latitude,location.longitude,ApiKey)