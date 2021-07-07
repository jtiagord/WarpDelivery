package edu.isel.pdm.warperapplication.web

data class Delivery (
    val deliveryId : Long?,
    val warperUsername: String?,
    val clientUsername : String?,
    val storeId: Long,
    val state : String,
    val clientPhone: String,
    val purchaseDate : String?,
    var deliverDate : String?,
    var rating : Int?,
    val deliverLatitude: Double,
    val deliverLongitude: Double,
    val deliverAddress: String,
    var reward: Float?,
    val type : String,
)

