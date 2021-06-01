package dataAccess.mappers

import DataAccess.mappers.DataMapper
import dataAccess.DAO.Vehicle
import org.jdbi.v3.core.Jdbi

class VehicleMapper(jdbi: Jdbi) : DataMapper<List<String>, Vehicle>(jdbi) {
    companion object {
        const val VEHICLE_TABLE = "DELIVERY"
    }

    override fun Create(DAO: Vehicle) {
        jdbi.useTransaction<Exception> { handle ->

            handle.createUpdate(
                "Insert Into $VEHICLE_TABLE" +
                        "(username, vehicletype , vehicleregistration) values " +
                        "(:username,:vehicletype,:vehicleregistration)"
            )
                .bind("username", DAO.username)
                .bind("vehicletype", DAO.vehicleType)
                .bind("vehicleregistration", DAO.vehicleRegistration)
                .execute()

            handle.commit()
        }
    }

    override fun Read(key: List<String>): Vehicle =
        jdbi.inTransaction<Vehicle, Exception> { handle ->
            val vehicle = handle.createQuery(
                "SELECT *" +
                        "from $VEHICLE_TABLE " +
                        "where username = :username " +
                        "and vehicleregistration= :vehicleregistration"
            )
                .bind("username", key[0])
                .bind("vehicleregistration", key[1])
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

    override fun Update(DAO: Vehicle) {
    }

    override fun Delete(key: List<String>) {
        jdbi.useTransaction<Exception> { handle ->
            handle.createUpdate(
                "DELETE from $VEHICLE_TABLE " +
                        "where username = :username " +
                        "and vehicleregistration= :vehicleregistration"
            )
                .bind("username", key[0])
                .bind("vehicleregistration", key[1])
                .execute()
        }
    }
}