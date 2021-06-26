package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.common.*
import com.isel.warpDelivery.dataAccess.mappers.DeliveryMapper
import com.isel.warpDelivery.dataAccess.DAO.Delivery
import com.isel.warpDelivery.dataAccess.mappers.ClientMapper
import com.isel.warpDelivery.model.ActiveWarperRepository
import com.isel.warpDelivery.dataAccess.mappers.StoreMapper
import com.isel.warpDelivery.exceptionHandler.ProblemJsonModel
import com.isel.warpDelivery.inputmodels.RequestDeliveryInputModel
import com.isel.warpDelivery.model.ActiveWarper
import com.isel.warpDelivery.model.Location
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(DELIVERIES_PATH)
class DeliveryController(val deliveryMapper: DeliveryMapper, val storeMapper : StoreMapper,
                         val clientMapper: ClientMapper, val activeWarpers: ActiveWarperRepository) {

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
        return deliveryMapper.read(deliveryId)
    }

    @PostMapping
    fun requestDelivery(@RequestBody delivery : RequestDeliveryInputModel) : ResponseEntity<Any>  {

        val store = storeMapper.read(delivery.storeId) ?: return ResponseEntity.badRequest().
            body("The store doesn't exist")

        val storeLocation = Location(store.latitude, store.longitude)

        if(storeLocation.getDistance(delivery.deliveryLocation) > MAX_DISTANCE_TO_STORE)
            return ResponseEntity.badRequest().body("The distance from delivery and the store are too large")

        val closestWarper = activeWarpers.getClosest(storeLocation, delivery.deliverySize)


        if(closestWarper != null) {
            println("found")
            val username = clientMapper.getUsernameByPhone(delivery.userPhone)
            deliveryMapper.create(delivery.toDelivery(closestWarper.username, username!!))
            return ResponseEntity.ok(closestWarper)
        }

        return ResponseEntity.notFound().build()
    }

    //Exception Handlers
    @ExceptionHandler(DeliveryMapper.DeliveryNotFoundException::class)
    fun handleNotFound(ex: Exception) = ResponseEntity
        .status(404)
            .contentType(ProblemJsonModel.MEDIA_TYPE)
            .body(
                ProblemJsonModel(
                    detail = ex.message,
                    type = URI("/probs/resource-not-found")
                )
            )

}