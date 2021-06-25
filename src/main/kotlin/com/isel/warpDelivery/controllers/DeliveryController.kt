package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.common.*
import com.isel.warpDelivery.dataAccess.mappers.DeliveryMapper
import com.isel.warpDelivery.dataAccess.DAO.Delivery
import com.isel.warpDelivery.model.ActiveWarperRepository
import com.isel.warpDelivery.dataAccess.mappers.StoreMapper
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(DELIVERIES_PATH)
class DeliveryController(val deliveryMapper: DeliveryMapper, val storeMapper : StoreMapper, val activeWarpers: ActiveWarperRepository) {

    companion object{
        const val MAX_DISTANCE_TO_STORE = 30000
    }
    @PostMapping("/{deliveryId}/state")
    fun updateDeliveryState(
        req: HttpServletRequest,
        @PathVariable deliveryId: String,
        newState: String
    ){
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
    fun getDelivery(req: HttpServletRequest, @RequestParam deliveryId: Long) : Delivery {
        return deliveryMapper.read(deliveryId)
    }

 /*   @PostMapping
    fun addDelivery(req: HttpServletRequest, @RequestBody delivery: DeliveryInputModel) {
        return deliveryMapper.create(delivery.toDelivery())
    }

    @PostMapping
    fun requestDelivery(@RequestBody delivery : RequestDeliveryInputModel)  : Any  {

        if(delivery.storeLocation.getDistance(delivery.deliveryLocation) > MAX_DISTANCE_TO_STORE)
            return ResponseEntity.badRequest().body("The distance from delivery and the store are too large")

        val closestWarper = activeWarpers.getClosest(delivery.storeLocation)
        return closestWarper ?: ResponseEntity.notFound()
    }*/
}