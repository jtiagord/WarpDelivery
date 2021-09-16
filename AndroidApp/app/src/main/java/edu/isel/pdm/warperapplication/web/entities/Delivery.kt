package edu.isel.pdm.warperapplication.web.entities

data class Delivery (
    val deliveryId : String?,
    val warperUsername: String?,
    val clientUsername : String?,
    val storeId: String,
    val state : String,
    val clientPhone: String,
    val purchaseDate : String?,
    var deliverDate : String?,
    val deliverLatitude: Double,
    val deliverLongitude: Double,
    val deliverAddress: String,
    val storeName: String,
    val type : String,
)

data class DeliveryFullInfo (
    val deliveryId : String,
    val warper : WarperInfo?,
    val store: StoreInfo,
    val state : String,
    val purchaseDate : String,
    var deliverDate : String,
    val deliverAddress: String,
    val clientPhone : String,
    val type : String
)

data class WarperInfo (
    val firstname: String,
    val lastname: String,
    val phonenumber: String,
    val email: String,
)

data class StoreInfo(
    val name : String,
    val postalcode : String,
    val address : String
)

