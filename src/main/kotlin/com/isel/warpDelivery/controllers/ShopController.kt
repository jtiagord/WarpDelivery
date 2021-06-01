package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.model.WarperList
import isel.warpDelivery.inputmodels.RequestDeliveryInputModel
import org.springframework.web.bind.annotation.*

@RestController
class ShopController(val activeWarpers : WarperList) {


    @PostMapping("addDelivery")
    fun requestDelivery(@RequestBody input : RequestDeliveryInputModel?) : String {
        println("DISTANCE : ${input!!.storeLocation.getDistance(input.deliveryLocation)}")
        // return activeWarpers.getClosest(input.storeLocation)!!
        return "a"
    }
}