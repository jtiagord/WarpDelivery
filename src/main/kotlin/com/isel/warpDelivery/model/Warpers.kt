package com.isel.warpDelivery.model

import com.isel.warpDelivery.dataAccess.mappers.DeliveryMapper
import com.isel.warpDelivery.dataAccess.DAO.Delivery
import com.isel.warpDelivery.dataAccess.DAO.StateTransition
import com.isel.warpDelivery.dataAccess.DAO.Vehicle
import com.isel.warpDelivery.dataAccess.DAO.Warper
import com.isel.warpDelivery.dataAccess.mappers.StateMapper
import com.isel.warpDelivery.dataAccess.mappers.TransitionMapper
import com.isel.warpDelivery.dataAccess.mappers.VehicleMapper
import com.isel.warpDelivery.dataAccess.mappers.WarperMapper
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

@Component
class Warpers(private val jdbi:Jdbi) {

    fun getAllWarpers(): List<Warper> {
        return jdbi.onDemand(WarperMapper::class.java).readAll()
    }

    fun getWarper(warper: String): Warper {
        return jdbi.onDemand(WarperMapper::class.java).read(warper)
    }

    fun addWarper(warper:Warper) {
        return jdbi.onDemand(WarperMapper::class.java).create(warper)
    }

    fun deleteWarper(warper:String){
        return jdbi.onDemand(WarperMapper::class.java).delete(warper)
    }

    fun updateWarper(warper:Warper){
        return jdbi.onDemand(WarperMapper::class.java).update(warper)
    }

    //-------------------------------------------------------------------------------

    fun getVehicles(username: String): List<Vehicle> {
        return jdbi.onDemand(VehicleMapper::class.java).readAll(username)
    }

    fun getVehicle(username: String,vehicleRegistration: String): Vehicle {
        return jdbi.onDemand(VehicleMapper::class.java).read(
            listOf(username,vehicleRegistration)
        )
    }

    fun addVehicle(vehicle: Vehicle){
        return jdbi.onDemand(VehicleMapper::class.java).create(vehicle)
    }

    fun deleteVehicle(username: String,vehicleRegistration:String) {
        return jdbi.onDemand(VehicleMapper::class.java).delete(
            listOf(username,vehicleRegistration)
        )
    }

    //-------------------------------------------------------------------------------

    fun getState(username: String): String {
        return jdbi.onDemand(StateMapper::class.java).read(username).state
    }

    fun updateState(state: Warper) {
        return jdbi.onDemand(StateMapper::class.java).update(state)
    }

    //-------------------------------------------------------------------------------

  /*  fun getDeliveries(username: String): List<Delivery> {
            return jdbi.onDemand(DeliveryMapper::class.java).readAll(username)
    }*/

    fun addDelivery(delivery: Delivery) {
        return jdbi.onDemand(DeliveryMapper::class.java).update(delivery)
    }

    fun getDelivery(username: String, deliveryId: String): Delivery {
        return jdbi.onDemand(DeliveryMapper::class.java).read(deliveryId)
    }

    fun updateDelivery(username: String, delivery: Delivery){
        return jdbi.onDemand(DeliveryMapper::class.java).update(delivery)
    }

    //-------------------------------------------------------------------------------

    fun getDeliveryTransitions(username: String): List<StateTransition>{
        return jdbi.onDemand(TransitionMapper::class.java).readAll(username)
    }
}
