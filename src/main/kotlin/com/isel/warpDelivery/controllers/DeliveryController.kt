package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.common.*
import com.isel.warpDelivery.dataAccess.mappers.DeliveryMapper
import com.isel.warpDelivery.dataAccess.DAO.Delivery
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(DELIVERIES_PATH)
class DeliveryController(val deliveryMapper: DeliveryMapper) {

    @PostMapping("/{deliveryId}/state")
    fun updateDeliveryState(
        req: HttpServletRequest,
        @PathVariable deliveryId: String,
        newState: String
    ) {
        return deliveryMapper.updateState(deliveryId, newState)
    }

    @GetMapping
    fun getAllDeliveries(
        req: HttpServletRequest,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") limit: Int
    ): List<Delivery> {
        return deliveryMapper.readAll()
    }

    @GetMapping("/{deliveryId}")
    fun getDelivery(req: HttpServletRequest, @RequestParam deliveryId: Int) : Delivery {
        return deliveryMapper.read(deliveryId)
    }
}