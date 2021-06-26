package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.common.WARPERS_PATH
import com.isel.warpDelivery.dataAccess.DAO.Delivery
import com.isel.warpDelivery.dataAccess.DAO.Vehicle
import com.isel.warpDelivery.dataAccess.mappers.*
import com.isel.warpDelivery.inputmodels.*
import com.isel.warpDelivery.model.ActiveWarper
import com.isel.warpDelivery.model.ActiveWarperRepository
import com.isel.warpDelivery.outputmodels.WarperOutputModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI


@RestController
@RequestMapping(WARPERS_PATH)
class WarperController(
    val warperMapper: WarperMapper, val deliveryMapper: DeliveryMapper, val vehicleMapper: VehicleMapper,
    val activeWarpers: ActiveWarperRepository
)
{
    var logger: Logger = LoggerFactory.getLogger(WarperController::class.java)

    companion object {
        const val WARPER_INACTIVE = "INACTIVE"
        const val WARPER_ACTIVE = "ACTIVE"
}




    @GetMapping("/{username}")
    fun getWarper(@PathVariable username: String): ResponseEntity<WarperOutputModel> {
        val warper = warperMapper.read(username) ?: throw WarperNotFoundException("Warper Not Found")
        return ResponseEntity.ok().body(warper.toOutputModel())
    }

    @PostMapping
    fun addWarper(@RequestBody warper: WarperInputModel): ResponseEntity<Any> {
        val warperCreated = warperMapper.create(warper.toDao()) //TODO: NOTHING IS BEING RETURNED, CHANGE RESPONSE BODY OR FIX
        return ResponseEntity.created(URI("$WARPERS_PATH/$warperCreated")).build()
    }


    @DeleteMapping("/{username}")
    fun deleteWarper(@PathVariable username: String): ResponseEntity<String> {
        warperMapper.delete(username)
        return ResponseEntity.status(204).build()
    }

    //-------------------------------------------------------------------------------
/*
    @GetMapping("/{username}/vehicles/{vehicleRegistration}")
    fun getVehicle(@PathVariable username: String, vehicleRegistration: String): ResponseEntity<Vehicle> {
        val vehicle = vehicleMapper.read(VehicleKey(username,vehicleRegistration))
        return ResponseEntity.ok().body(vehicle)
    }

    @GetMapping("/{username}/vehicles")
    fun getVehicles(@PathVariable username: String): ResponseEntity<List<Vehicle>> {
        val vehicles = vehicleMapper.readAll(username)
        return ResponseEntity.ok().body(vehicles)
    }


    @DeleteMapping("/{username}/vehicles/{vehicleRegistration}")
    fun deleteVehicle(@PathVariable username: String, vehicleRegistration: String): ResponseEntity<String>{
        vehicleMapper.delete(VehicleKey(username,vehicleRegistration))
        return ResponseEntity.status(204).build()
    }

      @GetMapping("/{username}/deliveries/{deliveryId}/transitions")
    fun getDeliveryTransitions(@PathVariable deliveryId: Long): ResponseEntity<List<StateTransition>> {
        val transitions = deliveryMapper.getTransitions(deliveryId)
        return ResponseEntity.ok().body(transitions)
    }


    //-------------------------------------------------------------------------------
*/

    @PutMapping("/{username}/vehicles")
    fun addVehicle(
        @PathVariable username: String,
        @RequestBody vehicle: VehicleInputModel
    ): ResponseEntity<String> {
        val vehicleDao = Vehicle(username, vehicle.type, vehicle.registration)
        vehicleMapper.create(vehicleDao)
        return ResponseEntity.status(200).build()
    }


    /// NEED TESTING STILL
    @GetMapping("/{username}/deliveries/{deliveryId}")
    fun getDelivery(@PathVariable deliveryId: Long): ResponseEntity<Delivery> {
        val delivery = deliveryMapper.read(deliveryId)
        return ResponseEntity.status(200).body(delivery)
    }

    @GetMapping("/{username}/deliveries")
    fun getDeliveries(@PathVariable username: String): ResponseEntity<List<Delivery>> {
        val deliveries = deliveryMapper.getDeliveriesByUsername(username)
        return ResponseEntity.status(200).body(deliveries)
    }

    //-------------------------------------------------------------------------------
/*

 */

    /* ROUTING RELATED ENDPOINTS */

    @PostMapping("/SetActive")
    fun addActiveWarper(@RequestBody warper: ActiveWarperInputModel) : ResponseEntity<Any>{
        logger.info(warper.toString())
        val warperInfo = warperMapper.read(warper.username) ?: throw WarperNotFoundException("Warper doesn't exist")

        val warperVehicle = warperInfo.vehicles.find { it.vehicleRegistration == warper.vehicle} ?:
        return ResponseEntity.status(400).body("Vehicle not found")

        val size = Size.fromText(warperVehicle.vehicleType)?:
        return ResponseEntity.status(400).body("Vehicle not found")

        activeWarpers.add(ActiveWarper(warper.username, warper.location, size ,warper.notificationToken))

        return ResponseEntity.status(200).body("OK")
    }

    @PutMapping("/location")
    fun updateWarperLocation(@RequestBody warper: ActiveWarperInputModel){
        activeWarpers.updateLocation(warper.username,warper.location)
    }

    @PutMapping("/SetInactive")
    fun removeActiveWarper(@RequestBody warper: ActiveWarperInputModel){
        activeWarpers.remove(warper.username)
    }



}


