package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.common.WARPERS_PATH
import com.isel.warpDelivery.dataAccess.DAO.Delivery
import com.isel.warpDelivery.dataAccess.DAO.StateTransition
import com.isel.warpDelivery.dataAccess.DAO.Vehicle
import com.isel.warpDelivery.dataAccess.DAO.Warper
import com.isel.warpDelivery.dataAccess.mappers.*
import com.isel.warpDelivery.inputmodels.RequestActiveWarperInputModel
import com.isel.warpDelivery.inputmodels.VehicleInputModel
import com.isel.warpDelivery.inputmodels.WarperInputModel
import com.isel.warpDelivery.inputmodels.toDao
import com.isel.warpDelivery.model.ActiveWarperRepository
import com.isel.warpDelivery.model.WarperLocation
import com.isel.warpDelivery.outputmodels.WarperOutputModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping(WARPERS_PATH)
class WarperController(
    val warperMapper: WarperMapper, val deliveryMapper: DeliveryMapper, val vehicleMapper: VehicleMapper,
    val stateMapper: StateMapper, val activeWarpers: ActiveWarperRepository
) {

    companion object {
        const val WARPER_INACTIVE = "INACTIVE"
        const val WARPER_ACTIVE = "ACTIVE"
    }




    @GetMapping("/{username}")
    fun getWarper(@PathVariable username: String): ResponseEntity<WarperOutputModel> {
        val warper = warperMapper.read(username).toOutputModel()
        return ResponseEntity.ok().body(warper)
    }

    @PostMapping
    fun addWarper(@RequestBody warper: WarperInputModel): ResponseEntity<Any> {
        val warperCreated = warperMapper.create(warper.toDao()) //TODO: NOTHING IS BEING RETURNED, CHANGE RESPONSE BODY OR FIX
        return ResponseEntity.status(201).build()
    }


    @DeleteMapping("/{username}")
    fun deleteWarper(@PathVariable username: String): ResponseEntity<String> {
        warperMapper.delete(username)
        return ResponseEntity.status(204).build()
    }

    //-------------------------------------------------------------------------------

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

    @PostMapping("/{username}/vehicles")
    fun addVehicle(
        @PathVariable username: String,
        @RequestBody vehicle: VehicleInputModel
    ): ResponseEntity<String> {
        val vehicleDao = Vehicle(username, vehicle.type, vehicle.registration)
        vehicleMapper.create(vehicleDao)
        return ResponseEntity.status(201).build()
    }

    @DeleteMapping("/{username}/vehicles/{vehicleRegistration}")
    fun deleteVehicle(@PathVariable username: String, vehicleRegistration: String): ResponseEntity<String>{
        vehicleMapper.delete(VehicleKey(username,vehicleRegistration))
        return ResponseEntity.status(204).build()
    }


    //-------------------------------------------------------------------------------

    @GetMapping("/{username}/state")
    fun getState(@PathVariable username: String): ResponseEntity<Any> {
        val state = stateMapper.read(username)
        //TODO: FIX
        return ResponseEntity.status(200).body(state)

    }


    @PutMapping("/{username}/state")
    fun updateState(@PathVariable state: Warper, @PathVariable username: String) {
        //TODO: FIX
    }


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

    @GetMapping("/{username}/deliveries/{deliveryId}/transitions")
    fun getDeliveryTransitions(@PathVariable deliveryId: Long): ResponseEntity<List<StateTransition>> {
        val transitions = deliveryMapper.getTransitions(deliveryId)
        return ResponseEntity.ok().body(transitions)
    }


    /* ROUTING RELATED ENDPOINTS */

    @PostMapping("/SetActive")
    fun addActiveWarper(@RequestBody warper: RequestActiveWarperInputModel){
      activeWarpers.add(WarperLocation(warper.username, warper.location,warper.messageToken))
    }

    @PutMapping("/location")
    fun updateWarperLocation(@RequestBody warper: RequestActiveWarperInputModel){
        activeWarpers.updateLocation(warper.username,warper.location)
    }
}


