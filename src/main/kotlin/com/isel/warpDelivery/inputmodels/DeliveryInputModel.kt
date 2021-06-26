package com.isel.warpDelivery.inputmodels

import com.isel.warpDelivery.dataAccess.DAO.Delivery
import com.isel.warpDelivery.model.Location
import java.sql.Timestamp

data class DeliveryInputModel(
    val storeId : Long,
    val deliverySize : Size,
    val clientUsername: String,
    val clientPhoneNumber: String,
    val purchaseDate: Timestamp,
    val address : String,
    val deliveryLocation : Location
)


/*fun DeliveryInputModel.toDelivery() = Delivery(0, this.clientUsername, null, "Em processamento",
    this.storeId, this.clientPhoneNumber, this.purchaseDate, null, null,
    this.price, null, this.type, emptyList())*/