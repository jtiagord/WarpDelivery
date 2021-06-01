package dataAccess.mappers

import DataAccess.mappers.DataMapper
import dataAccess.DAO.Vehicle
import dataAccess.DAO.Warper
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Repository

class WarperMapper(jdbi: Jdbi) : DataMapper<String, Warper>(jdbi) {

    companion object {
        const val USER_TABLE = "USERS"
        const val WARPER_TABLE = "WARPER"
        const val VEHICLE_TABLE = "VEHICLE"
    }

    override fun Create(DAO: Warper) {
        //TODO: ADD PHOTO
        jdbi.useTransaction<Exception> { handle ->

            handle.createUpdate(
                "Insert Into $USER_TABLE " +
                        "(username, firstname , lastname, phonenumber, password, email) values " +
                        "(:username,:firstname,:lastname,:phonenumber,:password,:email)"
            )
                .bind("username", DAO.username)
                .bind("firstname", DAO.firstname)
                .bind("lastname", DAO.lastname)
                .bind("phonenumber", DAO.lastname)
                .bind("password", DAO.password)
                .bind("email", DAO.email)
                .execute()

            handle.createUpdate(
                "Insert Into $WARPER_TABLE " +
                        "(username) values " +
                        "(:name)"
            )
                .bind("name", DAO.username)
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
        }
    }

    override fun Read(key: String): Warper =
        jdbi.inTransaction<Warper, Exception> { handle ->
            val warper = handle.createQuery(
                "SELECT username, firstname , lastname, phonenumber, password, email " +
                        "from $USER_TABLE " +
                        "where username = :username"
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

            return@inTransaction handle.createQuery(
                "SELECT username, firstname , lastname, phonenumber, password, email " +
                        "from $USER_TABLE"
            )
                .mapTo(Warper::class.java)
                .list()
        }

    override fun Update(DAO: Warper) {
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

    override fun Delete(key: String) {
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
}