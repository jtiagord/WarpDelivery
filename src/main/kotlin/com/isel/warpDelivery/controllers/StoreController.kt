package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.authentication.StoreResource
import com.isel.warpDelivery.authentication.USER_ATTRIBUTE_KEY
import com.isel.warpDelivery.authentication.UserInfo
import com.isel.warpDelivery.common.DELIVERIES_PATH
import com.isel.warpDelivery.common.STORE_PATH
import com.isel.warpDelivery.dataAccess.mappers.DeliveryMapper
import com.isel.warpDelivery.dataAccess.mappers.StoreMapper
import com.isel.warpDelivery.errorHandling.ApiException
import com.isel.warpDelivery.inputmodels.RequestDeliveryInputModel
import com.isel.warpDelivery.inputmodels.StoreInputModel
import com.isel.warpDelivery.inputmodels.toDao
import com.isel.warpDelivery.model.Location
import com.isel.warpDelivery.pubSub.DeliveryMessage
import com.isel.warpDelivery.pubSub.WarperPublisher

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(STORE_PATH)
class StoreController(val storeMapper : StoreMapper , val deliveryMapper : DeliveryMapper){

    @PostMapping
    fun addStore(@RequestBody store: StoreInputModel): ResponseEntity<Any> {
        val uuid = UUID.randomUUID().toString()
        val apiKey = uuid.replace("-","")
        val storeDao = store.toDao(apiKey)
        val storeCreated = storeMapper.create(storeDao)

        return ResponseEntity.created(URI("${STORE_PATH}/$storeCreated")).body(mapOf("apiKey" to apiKey))
    }

    @StoreResource
    @PostMapping("/requestDelivery")
    fun requestDelivery(req: HttpServletRequest, @RequestBody deliveryRequest : RequestDeliveryInputModel)
                                                                                                : ResponseEntity<Any> {

        val storeInfo = req.getAttribute(USER_ATTRIBUTE_KEY) as UserInfo

        val store = storeMapper.read(storeInfo.id) ?: throw ApiException("Store doesn't exist")

        val storeLocation = Location(store.latitude, store.longitude)

        if(storeLocation.getDistance(deliveryRequest.deliveryLocation) > DeliveryController.MAX_DISTANCE_TO_STORE)
            throw ApiException("The distance from delivery and the store are too large")


        val delivery = deliveryRequest.toDelivery(null, store.storeId!!)
        val deliveryId =  deliveryMapper.create(delivery)

        val messageToPublish =
            DeliveryMessage(deliveryId,storeLocation,store.address,store.storeId,
                deliveryRequest.deliveryLocation,deliveryRequest.address,deliveryRequest.deliverySize.text)

        WarperPublisher.publishDelivery(messageToPublish)


        return ResponseEntity.created(URI("$DELIVERIES_PATH/${deliveryId}")).build()
    }

}





