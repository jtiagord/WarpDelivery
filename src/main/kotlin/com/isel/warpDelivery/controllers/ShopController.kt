package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.model.WarperList
import com.isel.warpDelivery.inputmodels.RequestDeliveryInputModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class ShopController(val activeWarpers : WarperList) {


    @PostMapping("addDelivery")
    fun requestDelivery(@RequestBody input : RequestDeliveryInputModel)  : Any  {
        val closestWarper = activeWarpers.getClosest(input.storeLocation)
        return closestWarper ?: ResponseEntity<Any>(HttpStatus.NOT_FOUND)
    }
}