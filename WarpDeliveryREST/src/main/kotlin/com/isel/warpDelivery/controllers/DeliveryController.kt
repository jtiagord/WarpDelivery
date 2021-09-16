package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.authentication.AdminResource
import com.isel.warpDelivery.common.*
import com.isel.warpDelivery.dataAccess.mappers.DeliveryMapper
import com.isel.warpDelivery.dataAccess.dataClasses.Delivery
import com.isel.warpDelivery.dataAccess.dataClasses.DeliveryFullInfo
import com.isel.warpDelivery.model.ActiveWarperRepository
import com.isel.warpDelivery.dataAccess.mappers.StoreMapper
import com.isel.warpDelivery.errorHandling.ApiException
import com.isel.warpDelivery.model.NotificationSystem
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(DELIVERIES_PATH)
class DeliveryController(val deliveryMapper: DeliveryMapper, val storeMapper : StoreMapper,
                         val activeWarpers: ActiveWarperRepository,val notificationSystem: NotificationSystem) {

    companion object{
        const val MAX_DISTANCE_TO_STORE = 30000
    }

    @AdminResource
    @GetMapping
    fun getAllDeliveries(
        req: HttpServletRequest,
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int
    ): List<Delivery> {
        return deliveryMapper.readAll(limit, offset)
    }

    @GetMapping("/{deliveryId}")
    fun getDelivery(req: HttpServletRequest, @PathVariable deliveryId: String) : DeliveryFullInfo {
        return deliveryMapper.getDeliveryFullInfo(deliveryId) ?:
            throw ApiException("The delivery: $deliveryId doesn't exist", HttpStatus.NOT_FOUND)
    }





}