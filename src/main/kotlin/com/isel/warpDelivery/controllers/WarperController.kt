package com.isel.warpDelivery.controllers

import com.isel.warpDelivery.common.*
import com.isel.warpDelivery.model.Warpers
import com.isel.warpDelivery.dataAccess.DAO.Delivery
import com.isel.warpDelivery.dataAccess.DAO.StateTransition
import com.isel.warpDelivery.dataAccess.DAO.Vehicle
import com.isel.warpDelivery.dataAccess.DAO.Warper
import com.isel.warpDelivery.dataAccess.mappers.DeliveryMapper
import com.isel.warpDelivery.dataAccess.mappers.WarperMapper
import com.isel.warpDelivery.model.WarperList
import com.isel.warpDelivery.model.WarperLocation
import com.isel.warpDelivery.inputmodels.RequestActiveWarperInputModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(WARPERS_PATH)
class WarperController(val warperMapper: WarperMapper, val deliveryMapper: DeliveryMapper, val warpers: Warpers, val activeWarpers: WarperList) {

    //-------------------------------Warper related endpoints-------------------------
    @GetMapping
    fun getAllWarpers(): ResponseEntity<List<Warper>> {
        val warpers = warperMapper.readAll()
        return ResponseEntity.ok().body(warpers)
    }


    @GetMapping("/{username}")
    fun getWarper(@PathVariable username: String): ResponseEntity<Warper> {
        val warper = warperMapper.read(username)
        return ResponseEntity.ok().body(warper)
    }

    @PostMapping
    fun addWarper(@RequestBody warper: Warper): ResponseEntity<Unit> {
        val warper = warperMapper.create(warper)
        return ResponseEntity.status(201).body(warper)
    }


    @DeleteMapping("/{username}")
    fun deleteWarper(@PathVariable username: String): ResponseEntity<String> {
        warperMapper.delete(username)
        return ResponseEntity.status(204).body("Deleted user: $username")
    }


    @PutMapping("/{username}")
    fun updateWarper(@RequestBody warper: Warper) =
        ResponseEntity
            .status(204)
            .body(warpers.updateWarper(warper))

    //-------------------------------------------------------------------------------

    @GetMapping("/{username}/vehicles/{vehicleRegistration}")
    fun getVehicle(@PathVariable username: String, vehicleRegistration: String) =
        ResponseEntity
            .ok()
            .body(warpers.getVehicle(username, vehicleRegistration))

    @GetMapping("/{username}/vehicles")
    fun getVehicles(@PathVariable username: String) =
        ResponseEntity
            .ok()
            .body(warpers.getVehicles(username))

    @PostMapping("/{username}/vehicles")
    fun addVehicle(@RequestBody vehicle: Vehicle, @PathVariable username: String) =
        ResponseEntity
            .status(201)
            .body(warpers.addVehicle(vehicle))

    @DeleteMapping("/{username}/vehicles/{vehicleRegistration}")
    fun deleteVehicle(@PathVariable username: String, vehicleRegistration: String) =
        ResponseEntity
            .status(204)
            .body(warpers.deleteVehicle(username, vehicleRegistration))

    //-------------------------------------------------------------------------------

    @GetMapping("/{username}/state")
    fun getState(@PathVariable username: String) =
        ResponseEntity
            .ok()
            .body(warpers.getState(username))

    @PutMapping("/{username}/state")
    fun updateState(@PathVariable state: Warper, @PathVariable username: String) =
        ResponseEntity
            .status(204)
            .body(warpers.updateState(state))
    //-------------------------------------------------------------------------------

    /*@GetMapping(WARPER_DELIVERIES)
    fun getDeliveries(@PathVariable username:String)=
        ResponseEntity
            .ok()
            .body(warpers.getDeliveries(username))*/

    @PostMapping("/{username}/deliveries")
    fun addDelivery(@PathVariable delivery: Delivery, @PathVariable username: String) =
        ResponseEntity
            .status(201)
            .body(warpers.addDelivery(delivery))

    @GetMapping("/{username}/deliveries/{deliveryId}")
    fun getDelivery(@PathVariable username: String, @PathVariable deliveryId: Int) =
        ResponseEntity
            .ok()
            .body(warpers.getDelivery(username, deliveryId.toString()))

    @PutMapping("/{username}/deliveries/{deliveryId}")
    fun updateDelivery(@PathVariable username: String, @RequestBody delivery: Delivery) =
        ResponseEntity
            .status(204)
            .body(warpers.updateDelivery(username, delivery))

    //-------------------------------------------------------------------------------

    @GetMapping("/{username}/deliveries/{deliveryId}/transitions")
    fun getDeliveryTransitions(@PathVariable deliveryId: Int): ResponseEntity<List<StateTransition>> {
        val transitions = deliveryMapper.getTransitions(deliveryId)
        return ResponseEntity.ok().body(transitions)
    }


    @PostMapping("SetActive")
    fun addActiveWarper(@RequestBody warper: RequestActiveWarperInputModel?) {

        activeWarpers.add(WarperLocation(warper!!.username, warper.currentLocation))
    }
}