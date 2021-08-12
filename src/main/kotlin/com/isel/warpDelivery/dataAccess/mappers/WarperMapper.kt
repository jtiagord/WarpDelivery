package com.isel.warpDelivery.dataAccess.mappers

import com.isel.warpDelivery.dataAccess.dataClasses.Vehicle
import com.isel.warpDelivery.dataAccess.dataClasses.Warper
import com.isel.warpDelivery.dataAccess.dataClasses.WarperEdit
import com.isel.warpDelivery.errorHandling.ApiException
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component


class WarperNotFoundException(s: String) : Exception(s)

@Component
class WarperMapper(val jdbi: Jdbi) {

    companion object {
        const val WARPER_TABLE = "WARPER"
        const val VEHICLE_TABLE = "VEHICLE"

        private data class USER_VERIFICATION(
            val username: String,
            val phonenumber: String,
            val email: String
        )
    }

    fun create(DAO: Warper): String =
        jdbi.inTransaction<String, Exception> { handle ->
            val optional = handle.createQuery(
                "SELECT username, phonenumber, email" +
                        " FROM $WARPER_TABLE WHERE " +
                        "username=:username OR email = :email OR phonenumber = :phonenumber"
            )
                .bind("username", DAO.username)
                .bind("phonenumber", DAO.phonenumber)
                .bind("email", DAO.email)
                .mapTo(USER_VERIFICATION::class.java)
                .findFirst()

            if (!optional.isEmpty) {
                val userVerification = optional.get()
                if (userVerification.username == DAO.username) {
                    throw ApiException("User Already Exists")
                }

                if (userVerification.email == DAO.email) {
                    throw ApiException("Email already Exists")
                }

                if (userVerification.phonenumber == DAO.phonenumber) {
                    throw ApiException("Phone Number Already Exists")
                }
            }

            handle.createUpdate(
                "Insert Into $WARPER_TABLE " +
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

            for (vehicle in DAO.vehicles) {
                handle.createUpdate(
                    "Insert Into $VEHICLE_TABLE " +
                            "(username, vehicleType, vehicleRegistration) values " +
                            "(:username, :type ,:registration)"
                )
                .bind("username", DAO.username)
                .bind("type", vehicle.type)
                .bind("registration", vehicle.registration)
                .execute()
            }
            return@inTransaction DAO.username
        }

    fun read(key: String): Warper? =
        jdbi.inTransaction<Warper, Exception> { handle ->
            val warperOpt = handle.createQuery(
                "SELECT username, firstname , lastname, phonenumber, email, password " +
                        "from $WARPER_TABLE " +
                        "where username = :username"
            )
            .bind("username", key)
            .mapTo(Warper::class.java)
            .findOne()


            val warper = if (warperOpt.isPresent) warperOpt.get() else return@inTransaction null

            val vehicles = handle.createQuery(
                "SELECT  username , vehicleType AS type, vehicleRegistration AS registration " +
                        "from $VEHICLE_TABLE " +
                        "where username = :username"
            )
                .bind("username", key)
                .mapTo(Vehicle::class.java)
                .list()

            warper.vehicles = vehicles

            return@inTransaction warper
        }

    fun update(warperInfo: WarperEdit, username: String) {


        var query = "update $WARPER_TABLE set "

        if(warperInfo.email != null)
            query += "email = :email, "
        if(warperInfo.firstname != null)
            query += "firstname = :firstname, "
        if(warperInfo.lastname != null)
            query += "lastname = :lastname, "
        if(warperInfo.phonenumber != null)
            query += "phonenumber = :phonenumber, "

        if(warperInfo.password != null)
            query += "password = :password, "

        query = query.dropLast(2)
        query += " where username = :username"

        
        jdbi.useTransaction<Exception> { handle ->

            val update = handle.createUpdate(query)

            if(warperInfo.email != null)
                update.bind("email", warperInfo.email)
            if(warperInfo.firstname != null)
                update.bind("firstname", warperInfo.firstname)
            if(warperInfo.lastname != null)
                update.bind("lastname", warperInfo.lastname)
            if(warperInfo.phonenumber != null)
                update.bind("phonenumber", warperInfo.phonenumber)
            if(warperInfo.password != null)
                update.bind("password", warperInfo.password)

            update.bind("username", username).execute()
        }
    }

    fun delete(key: String) {
        jdbi.useTransaction<Exception> { handle ->

            handle.createUpdate(
                "DELETE from $VEHICLE_TABLE " +
                        "where username = :username"
            )
            .bind("username", key).execute()

            handle.createUpdate(
                "DELETE from $WARPER_TABLE " +
                        "where username = :username"
            )
            .bind("username", key)
            .execute()
        }
    }


}