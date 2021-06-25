package com.isel.warpDelivery.dataAccess.mappers

import com.isel.warpDelivery.dataAccess.DAO.Vehicle
import com.isel.warpDelivery.dataAccess.DAO.Warper
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

@Component
class WarperMapper(jdbi: Jdbi) : DataMapper<String, Warper>(jdbi) {

    companion object {
        const val USER_TABLE = "USERS"
        const val WARPER_TABLE = "WARPER"
        const val VEHICLE_TABLE = "VEHICLE"
    }

    override fun create(DAO: Warper): String =
        //TODO: ADD PHOTO
        jdbi.inTransaction<String, Exception> { handle ->

            handle.createUpdate(
                "Insert Into $USER_TABLE " +
                        "(username, firstname , lastname, phonenumber, password, email) values " +
                        "(:username,:firstname,:lastname,:phonenumber,:password,:email)"
            )
                .bind("username", DAO.username)
                .bind("firstname", DAO.firstname)
                .bind("lastname", DAO.lastname)
                .bind("phonenumber", DAO.phonenumber)
                .bind("password", DAO.password)
                .bind("email", DAO.email)
                .execute()

            handle.createUpdate(
                "Insert Into $WARPER_TABLE " +
                        "(username, state) values " +
                        "(:name, :state)"
            )
                .bind("name", DAO.username)
                .bind("state", DAO.state)
                .execute()

            for (vehicle in DAO.vehicles) {
                handle.createUpdate(
                    "Insert Into $VEHICLE_TABLE " +
                            "(username, vehicleType, vehicleRegistration) values " +
                            "(:username, :type ,:registration)"
                )
                    .bind("username", DAO.username)
                    .bind("type", vehicle.vehicleType)
                    .bind("registration", vehicle.vehicleRegistration)
                    .execute()
            }
            handle.commit()
            return@inTransaction DAO.username
        }

    override fun read(key: String): Warper =
        jdbi.inTransaction<Warper, Exception> { handle ->
            val warper = handle.createQuery(
                "SELECT $USER_TABLE.username, firstname , lastname, phonenumber, email, password, state " +
                        "from $USER_TABLE JOIN $WARPER_TABLE ON $USER_TABLE.username = $WARPER_TABLE.username " +
                        "where $USER_TABLE.username = :username"
            )
                .bind("username", key)
                .mapTo(Warper::class.java)
                .one()

            val vehicles = handle.createQuery(
                "SELECT  username , vehicleType, vehicleRegistration " +
                        "from $VEHICLE_TABLE " +
                        "where username = :username"
            )
                .bind("username", key)
                .mapTo(Vehicle::class.java)
                .list()

            warper.vehicles = vehicles

            return@inTransaction warper
        }

    fun readAll(): List<Warper> =
        jdbi.inTransaction<List<Warper>, Exception> { handle ->
            val warpers = handle.createQuery(
                "SELECT $USER_TABLE.username, firstname , lastname, phonenumber, email, password, state " +
                        "from $USER_TABLE JOIN $WARPER_TABLE ON $USER_TABLE.username = $WARPER_TABLE.username "
            )
                .mapTo(Warper::class.java)
                .list()

            //TODO: MAYBE DO THIS WITH VEHICLE MAPPER ?
            for (warper in warpers) {
                val vehicles = handle.createQuery(
                    "SELECT  username , vehicleType, vehicleRegistration " +
                            "from $VEHICLE_TABLE " +
                            "where username = :username"
                )
                    .bind("username", warper.username)
                    .mapTo(Vehicle::class.java)
                    .list()

                warper.vehicles = vehicles
            }

            return@inTransaction warpers
        }

    override fun update(DAO: Warper) {
        jdbi.useTransaction<Exception> { handle ->
            handle.createUpdate(
                "update $USER_TABLE " +
                        "set username=:username, " +
                        "firstname=:firstname, " +
                        "lastname= :lastname, " +
                        "phonenumber:phonenumber, " +
                        "password:password, " +
                        "email:email" +
                        "where username=:username"
            )
                .bind("username", DAO.username)
                .bind("firstname", DAO.firstname)
                .bind("lastname", DAO.lastname)
                .bind("phonenumber", DAO.lastname)
                .bind("password", DAO.password)
                .bind("email", DAO.email)
                .execute()
        }
    }

    override fun delete(key: String) {
        jdbi.useTransaction<Exception> { handle ->
            handle.createUpdate(
                "DELETE from $USER_TABLE " +
                        "where username = :username"
            )
                .bind("username", key)
                .execute()
            /*handle.createUpdate(
                "DELETE from $VEHICLE_TABLE" +
                        "where username = :username"
            )
                .bind("username", key)
                .execute()

            handle.createUpdate(
                "DELETE from $WARPER_TABLE" +
                        "where username = :username"
            )
                .bind("username", key)
                .execute()*/
        }
    }

    fun getState(username: String): Any {
        return jdbi.inTransaction<String, Exception> { handle ->

            return@inTransaction handle.createQuery("SELECT state FROM $WARPER_TABLE WHERE username = :username")
                .bind("username", username)
                .mapTo(String::class.java)
                .one()
        }
    }
}