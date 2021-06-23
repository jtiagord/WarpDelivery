package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.model.ActiveWarperRepository
import com.isel.warpDelivery.inputmodels.RequestDeliveryInputModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class ShopController(val activeWarpers : ActiveWarperRepository) {

    companion object{
        const val MAX_DISTANCE_TO_STORE = 30_000
    }


}

