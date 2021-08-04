package com.isel.warpDelivery.dataAccess.mappers

import com.isel.warpDelivery.dataAccess.dataClasses.Vehicle
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component


data class VehicleKey(
    val userName : String,
    val vehicleRegistration : String
)
@Component
class VehicleMapper(val jdbi: Jdbi) {
    companion object {
        const val VEHICLE_TABLE = "VEHICLE"
    }

    fun create(DAO: Vehicle) : VehicleKey =
        jdbi.withHandle<VehicleKey,Exception> { handle ->

            handle.createUpdate(
                "Insert Into $VEHICLE_TABLE" +
                        "(username, vehicletype , vehicleregistration) values " +
                        "(:username,:vehicletype,:vehicleregistration) ON CONFLICT DO NOTHING"
            )
            .bind("username", DAO.username)
            .bind("vehicletype", DAO.type)
            .bind("vehicleregistration", DAO.registration)
            .execute()


            return@withHandle VehicleKey(DAO.username,DAO.registration)
        }


    fun read(key: VehicleKey): Vehicle =
        jdbi.inTransaction<Vehicle, Exception> { handle ->
            val vehicle = handle.createQuery(
                "SELECT username, vehicletype AS type, vehicleRegistration as registration " +
                        "from $VEHICLE_TABLE " +
                        "where username = :username " +
                        "and vehicleregistration= :vehicleregistration"
            )
            .bind("username", key.userName)
            .bind("vehicleregistration", key.vehicleRegistration)
            .mapTo(Vehicle::class.java)
            .findOne()

            return@inTransaction if (vehicle.isPresent) vehicle.get() else null
    }

    fun readAll(username:String): List<Vehicle> =
        jdbi.inTransaction<List<Vehicle>, Exception> { handle ->
            return@inTransaction handle.createQuery(
                "SELECT username, vehicletype AS type, vehicleRegistration as registration " +
                        "from $VEHICLE_TABLE " +
                        "where username = :username"
            )
                .bind("username", username)
                .mapTo(Vehicle::class.java)
                .list()
        }

    fun update(DAO: Vehicle) {
        TODO()
    }

    fun delete(key: VehicleKey) {
        jdbi.useTransaction<Exception> { handle ->
            handle.createUpdate(
                "DELETE from $VEHICLE_TABLE " +
                        "where vehicleregistration= :vehicleregistration"
            )
                .bind("vehicleregistration", key)
                .execute()
        }
    }
}

class VehicleAlreadyExistsException(s: String) : Exception(s)
