package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.authentication.StoreResource
import com.isel.warpDelivery.authentication.USER_ATTRIBUTE_KEY
import com.isel.warpDelivery.authentication.UserInfo
import com.isel.warpDelivery.common.*
import com.isel.warpDelivery.dataAccess.mappers.DeliveryMapper
import com.isel.warpDelivery.dataAccess.dataClasses.Delivery
import com.isel.warpDelivery.dataAccess.mappers.ClientMapper
import com.isel.warpDelivery.model.ActiveWarperRepository
import com.isel.warpDelivery.dataAccess.mappers.StoreMapper
import com.isel.warpDelivery.errorHandling.ApiException
import com.isel.warpDelivery.inputmodels.RequestDeliveryInputModel
import com.isel.warpDelivery.model.Location
import com.isel.warpDelivery.model.NotificationSystem
import com.isel.warpDelivery.pubSub.DeliveryMessage
import com.isel.warpDelivery.pubSub.WarperPublisher.publishDelivery
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
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
    fun getDelivery(req: HttpServletRequest, @PathVariable deliveryId: String) : Delivery {
        return deliveryMapper.read(deliveryId) ?:
            throw ApiException("The delivery: $deliveryId doesn't exist", HttpStatus.NOT_FOUND)
    }

    @StoreResource
    @PostMapping
    fun requestDelivery(req: HttpServletRequest, @RequestBody deliveryRequest : RequestDeliveryInputModel)  : ResponseEntity<Any> {

        val storeInfo = req.getAttribute(USER_ATTRIBUTE_KEY) as UserInfo

        val store = storeMapper.read(storeInfo.id) ?: throw ApiException("Store doesn't exist")

        val storeLocation = Location(store.latitude, store.longitude)

        if(storeLocation.getDistance(deliveryRequest.deliveryLocation) > MAX_DISTANCE_TO_STORE)
            throw ApiException("The distance from delivery and the store are too large")

        //val closestWarper = activeWarpers.getClosest(storeLocation, delivery.deliverySize)
        val username = clientMapper.getUsernameByPhone(deliveryRequest.userPhone)
        val delivery= deliveryRequest.toDelivery(null, username, store.storeId!!)
        val deliveryId =  deliveryMapper.create(delivery)

        val messageToPublish =
            DeliveryMessage(storeLocation,store.address,store.storeId,
                deliveryRequest.deliveryLocation,deliveryRequest.address,deliveryRequest.deliverySize.text)

        publishDelivery(messageToPublish)


        return ResponseEntity.created(URI("${DELIVERIES_PATH}/${deliveryId}")).build()
    }



}