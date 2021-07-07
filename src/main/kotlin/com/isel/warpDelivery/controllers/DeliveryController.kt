package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.common.*
import com.isel.warpDelivery.dataAccess.mappers.DeliveryMapper
import com.isel.warpDelivery.dataAccess.dataClasses.Delivery
import com.isel.warpDelivery.dataAccess.mappers.ClientMapper
import com.isel.warpDelivery.model.ActiveWarperRepository
import com.isel.warpDelivery.dataAccess.mappers.StoreMapper
import com.isel.warpDelivery.errorHandling.ApiException
import com.isel.warpDelivery.inputmodels.RequestDeliveryInputModel
import com.isel.warpDelivery.model.ActiveWarper
import com.isel.warpDelivery.model.Location
import com.isel.warpDelivery.model.NotificationSystem
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(DELIVERIES_PATH)
class DeliveryController(val deliveryMapper: DeliveryMapper, val storeMapper : StoreMapper,
                         val clientMapper: ClientMapper, val activeWarpers: ActiveWarperRepository,val notificationSystem: NotificationSystem) {

    companion object{
        const val MAX_DISTANCE_TO_STORE = 30000
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
    fun getDelivery(req: HttpServletRequest, @PathVariable deliveryId: Long) : Delivery {
        return deliveryMapper.read(deliveryId) ?:
            throw ApiException("The delivery: $deliveryId doesn't exist", HttpStatus.NOT_FOUND)
    }

    @PostMapping
    fun requestDelivery(@RequestBody delivery : RequestDeliveryInputModel)  : ActiveWarper {

        val store = storeMapper.read(delivery.storeId) ?: throw ApiException("Store doesn't exist")

        val storeLocation = Location(store.latitude, store.longitude)

        if(storeLocation.getDistance(delivery.deliveryLocation) > MAX_DISTANCE_TO_STORE)
            throw ApiException("The distance from delivery and the store are too large")

        val closestWarper = activeWarpers.getClosest(storeLocation, delivery.deliverySize)

        if(closestWarper != null) {

            val username = clientMapper.getUsernameByPhone(delivery.userPhone)

            deliveryMapper.create(delivery.toDelivery(closestWarper.username, username))
            notificationSystem.sendNotification(closestWarper)
            return closestWarper
        }
        else {
            throw ApiException("No warper was found")
        }
    }



}