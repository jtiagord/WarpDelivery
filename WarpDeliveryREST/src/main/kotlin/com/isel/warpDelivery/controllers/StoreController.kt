package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.authentication.AdminResource
import com.isel.warpDelivery.authentication.StoreResource
import com.isel.warpDelivery.authentication.USER_ATTRIBUTE_KEY
import com.isel.warpDelivery.authentication.UserInfo
import com.isel.warpDelivery.common.DELIVERIES_PATH
import com.isel.warpDelivery.common.STORE_PATH
import com.isel.warpDelivery.dataAccess.dataClasses.DeliveryState
import com.isel.warpDelivery.dataAccess.mappers.DeliveryMapper
import com.isel.warpDelivery.dataAccess.mappers.StoreMapper
import com.isel.warpDelivery.errorHandling.ApiException
import com.isel.warpDelivery.inputmodels.RequestDeliveryInputModel
import com.isel.warpDelivery.inputmodels.StoreInputModel
import com.isel.warpDelivery.inputmodels.toDao
import com.isel.warpDelivery.model.ActiveDelivery
import com.isel.warpDelivery.model.ActiveWarperRepository
import com.isel.warpDelivery.model.DeliveringWarperState
import com.isel.warpDelivery.model.Location
import com.isel.warpDelivery.pubSub.WarperPublisher
import org.springframework.http.HttpStatus

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(STORE_PATH)
class StoreController(val storeMapper : StoreMapper , val deliveryMapper : DeliveryMapper,
                        val warperRepository: ActiveWarperRepository){

    @AdminResource
    @PostMapping
    fun addStore(@RequestBody store: StoreInputModel): ResponseEntity<Any> {
        val uuid = UUID.randomUUID().toString()
        val apiKey = uuid.replace("-","")
        val storeDao = store.toDao(apiKey)
        val createdStoreId = storeMapper.create(storeDao)

        return ResponseEntity.created(URI("${STORE_PATH}/$createdStoreId")).body(
            mapOf("apiKey" to apiKey,"storeId" to createdStoreId))
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

        val deliveryLocation = Location(delivery.deliverLatitude, delivery.deliverLongitude)

        val deliveryToPublish =
            ActiveDelivery(deliveryId, delivery.type ,storeLocation, deliveryLocation)

        WarperPublisher.publishDelivery(deliveryToPublish)


        return ResponseEntity.created(URI("$DELIVERIES_PATH/${deliveryId}")).body(
            mapOf("deliveryId" to deliveryId))
    }

    @StoreResource
    @PostMapping("/deliveries/{deliveryId}/handleDelivery")
    fun handleDelivery(req: HttpServletRequest, @PathVariable deliveryId : String) {

        val storeInfo = req.getAttribute(USER_ATTRIBUTE_KEY) as UserInfo
        val warper = warperRepository.getWarperWithDeliveryId(deliveryId)
        val delivery = deliveryMapper.read(deliveryId) ?: throw ApiException("Delivery doesn't exist",
                HttpStatus.NOT_FOUND)

        if(delivery.storeId != storeInfo.id) throw ApiException("Delivery doesn't exist", HttpStatus.NOT_FOUND)

        if(warper == null){
            throw ApiException("There is no warper assigned to delivering your delivery")
        }
        warperRepository.updateState(warper.username, DeliveringWarperState.DELIVERING)
        deliveryMapper.updateStateAndAssignWarper(deliveryId, DeliveryState.DELIVERING, warper.username)
    }

    @StoreResource
    @PostMapping("/deliveries/{deliveryId}/cancelDelivery")
    fun cancelDelivery(req: HttpServletRequest, @PathVariable deliveryId : String) {

        val storeInfo = req.getAttribute(USER_ATTRIBUTE_KEY) as UserInfo
        val warper = warperRepository.getWarperWithDeliveryId(deliveryId)
        val delivery = deliveryMapper.read(deliveryId) ?:
                 throw ApiException("Delivery doesn't exist", HttpStatus.NOT_FOUND)

        if(delivery.storeId != storeInfo.id) throw ApiException("Delivery doesn't exist", HttpStatus.NOT_FOUND)

        if(delivery.state == DeliveryState.DELIVERING || delivery.state == DeliveryState.DELIVERED){
            throw ApiException("A delivery that has already been handled can't be canceled")
        }

        warperRepository.cancelDelivery(deliveryId)
        deliveryMapper.updateState(deliveryId, DeliveryState.CANCELLED)

    }

    @StoreResource
    @GetMapping("/deliveries")
    fun getDeliveries(req: HttpServletRequest,
                      @RequestParam(defaultValue = "10") limit : Int = 10,
                      @RequestParam(defaultValue = "0") offset : Int = 0) : ResponseEntity<Any> {

        val storeInfo = req.getAttribute(USER_ATTRIBUTE_KEY) as UserInfo
        val store = storeMapper.read(storeInfo.id) ?: throw ApiException("Store doesn't exist")
        val deliveries = deliveryMapper.getDeliveriesByStoreId(storeInfo.id,limit, offset)
        return ResponseEntity.status(200).body(deliveries)
    }
}





