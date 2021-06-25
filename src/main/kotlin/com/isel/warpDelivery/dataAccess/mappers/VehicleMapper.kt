package com.isel.warpDelivery.dataAccess.mappers

import com.isel.warpDelivery.dataAccess.DAO.Vehicle
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component


data class VehicleKey(
    val userName : String,
    val vehicleRegistration : String
)
@Component
class VehicleMapper(jdbi: Jdbi) : DataMapper<VehicleKey, Vehicle>(jdbi) {
    companion object {
        const val VEHICLE_TABLE = "VEHICLE"
    }

    override fun create(DAO: Vehicle) : VehicleKey =
        jdbi.withHandle<VehicleKey,Exception> { handle ->

            handle.createUpdate(
                "Insert Into $VEHICLE_TABLE" +
                        "(username, vehicletype , vehicleregistration) values " +
                        "(:username,:vehicletype,:vehicleregistration)"
            )
                .bind("username", DAO.username)
                .bind("vehicletype", DAO.vehicleType)
                .bind("vehicleregistration", DAO.vehicleRegistration)
                .execute()

            return@withHandle VehicleKey(DAO.username,DAO.vehicleRegistration)
        }


    override fun read(key: VehicleKey): Vehicle =
        jdbi.inTransaction<Vehicle, Exception> { handle ->
            val vehicle = handle.createQuery(
                "SELECT *" +
                        "from $VEHICLE_TABLE " +
                        "where username = :username " +
                        "and vehicleregistration= :vehicleregistration"
            )
            .bind("username", key.userName)
            .bind("vehicleregistration", key.vehicleRegistration)
            .mapTo(Vehicle::class.java)
            .one()

            return@inTransaction vehicle
    }

    fun readAll(username:String): List<Vehicle> =
        jdbi.inTransaction<List<Vehicle>, Exception> { handle ->
            return@inTransaction handle.createQuery(
                "SELECT * " +
                        "from $VEHICLE_TABLE " +
                        "where username = :username"
            )
                .bind("username", username)
                .mapTo(Vehicle::class.java)
                .list()
        }

    override fun update(DAO: Vehicle) {
        TODO()
    }

    override fun delete(key: VehicleKey) {
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