package com.isel.warpDelivery.Model

import DataAccess.mappers.DeliveryMapper
import dataAccess.DAO.Delivery
import dataAccess.DAO.StateTransition
import dataAccess.DAO.Vehicle
import dataAccess.DAO.Warper
import dataAccess.mappers.StateMapper
import dataAccess.mappers.TransitionMapper
import dataAccess.mappers.VehicleMapper
import dataAccess.mappers.WarperMapper
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

@Component
class Warpers(private val jdbi:Jdbi) {

    fun getAllWarpers(): List<Warper> {
        return jdbi.onDemand(WarperMapper::class.java).readAll()
    }

    fun getWarper(warper: String): Warper {
        return jdbi.onDemand(WarperMapper::class.java).Read(warper)
    }

    fun addWarper(warper:Warper) {
        return jdbi.onDemand(WarperMapper::class.java).Create(warper)
    }

    fun deleteWarper(warper:String){
        return jdbi.onDemand(WarperMapper::class.java).Delete(warper)
    }

    fun updateWarper(warper:Warper){
        return jdbi.onDemand(WarperMapper::class.java).Update(warper)
    }

    //-------------------------------------------------------------------------------

    fun getVehicles(username: String): List<Vehicle> {
        return jdbi.onDemand(VehicleMapper::class.java).readAll(username)
    }

    fun getVehicle(username: String,vehicleRegistration: String): Vehicle {
        return jdbi.onDemand(VehicleMapper::class.java).Read(
            listOf(username,vehicleRegistration)
        )
    }

    fun addVehicle(vehicle: Vehicle){
        return jdbi.onDemand(VehicleMapper::class.java).Create(vehicle)
    }

    fun deleteVehicle(username: String,vehicleRegistration:String) {
        return jdbi.onDemand(VehicleMapper::class.java).Delete(
            listOf(username,vehicleRegistration)
        )
    }

    //-------------------------------------------------------------------------------

    fun getState(username: String): String {
        return jdbi.onDemand(StateMapper::class.java).Read(username).state
    }

    fun updateState(state: Warper) {
        return jdbi.onDemand(StateMapper::class.java).Update(state)
    }

    //-------------------------------------------------------------------------------

    fun getDeliveries(username: String): List<Delivery> {
            return jdbi.onDemand(DeliveryMapper::class.java).readAll(username)
    }

    fun addDelivery(delivery: Delivery) {
        return jdbi.onDemand(DeliveryMapper::class.java).Update(delivery)
    }

    fun getDelivery(username: String, deliveryId: String): Delivery {
        return jdbi.onDemand(DeliveryMapper::class.java).Read(deliveryId)
    }

    fun updateDelivery(username: String, delivery: Delivery){
        return jdbi.onDemand(DeliveryMapper::class.java).Update(delivery)
    }

    //-------------------------------------------------------------------------------

    fun getDeliveryTransitions(username: String): List<StateTransition>{
        return jdbi.onDemand(TransitionMapper::class.java).readAll(username)
    }
}
