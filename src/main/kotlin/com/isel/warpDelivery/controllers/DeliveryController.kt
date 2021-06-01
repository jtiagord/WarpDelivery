package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.dataAccess.mappers.DeliveryMapper
import com.isel.warpDelivery.common.DELIVERIES
import com.isel.warpDelivery.common.DELIVERY
import com.isel.warpDelivery.common.DELIVERY_STATE
import com.isel.warpDelivery.dataAccess.DAO.Delivery
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
class DeliveryController(val deliveryMapper: DeliveryMapper) {

    @PostMapping(DELIVERY_STATE)
    fun updateDeliveryState(
        req: HttpServletRequest,
        @PathVariable DeliveryId: String,
        newState: String
    ) {
        return deliveryMapper.updateState(DeliveryId, newState)
    }

    @GetMapping(DELIVERIES)
    fun getAllDeliveries(
        req: HttpServletRequest,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") limit: Int
    ): List<Delivery> {
        return deliveryMapper.readAll()
    }

    @GetMapping(DELIVERY)
    fun getDelivery(req: HttpServletRequest, @RequestParam id: String) : Delivery {
        return deliveryMapper.read(id)
    }
}