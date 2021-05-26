package isel.warpDelivery.Controllers

import isel.warpDelivery.inputmodels.RequestDeliveryInputModel
import isel.warpDelivery.model.Warper
import isel.warpDelivery.model.WarperList
import org.springframework.web.bind.annotation.*

@RestController
class ShopController(val activeWarpers : WarperList) {


    @PostMapping("addDelivery")
    fun RequestDelivery(@RequestBody input : RequestDeliveryInputModel) : String {
        println("DISTANCE : ${input.storeLocation.getDistance(input.deliveryLocation)}")
        // return activeWarpers.getClosest(input.storeLocation)!!
        return "a"
    }
}