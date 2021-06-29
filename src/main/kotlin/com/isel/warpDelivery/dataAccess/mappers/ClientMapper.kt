package com.isel.warpDelivery.dataAccess.mappers

import com.isel.warpDelivery.dataAccess.DAO.Address
import com.isel.warpDelivery.dataAccess.DAO.Client
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

@Component
class ClientMapper(jdbi: Jdbi) : DataMapper<String, Client>(jdbi) {
    companion object {
        const val USER_TABLE = "USERS"
        const val CLIENT_ADDRESSES_TABLE = "CLIENT_ADDRESS"
        const val DELIVERIES_TABLE = "DELIVERY"


        private data class ADDRESS_VERIFICATION(
            val addressId: Long,
            val username : String
        )

        private data class USER_VERIFICATION(
            val username : String,
            val phonenumber: String,
            val email : String
        )
    }

    override fun create(DAO: Client) : String =
        jdbi.inTransaction<String,Exception> { handle ->

            val userOptional = handle.createQuery("SELECT username, phonenumber, email" +
                    " FROM ${WarperMapper.USER_TABLE} WHERE " +
                    "username=:username OR email = :email OR phonenumber = :phonenumber")
                .bind("username", DAO.username)
                .bind("phonenumber", DAO.phonenumber)
                .bind("email", DAO.email)
                .mapTo(USER_VERIFICATION::class.java)
                .findFirst()

            if(!userOptional.isEmpty){
                val userVerification = userOptional.get()
                if(userVerification.username == DAO.username){
                    throw UserAlreadyExistsException("The user: ${DAO.username} already exists")
                }

                if(userVerification.email == DAO.email){
                    throw EmailAlreadyExistsException("The email: ${DAO.email} already exists")
                }

                if(userVerification.phonenumber == DAO.phonenumber){
                    throw PhoneAlreadyExistsException("The phone number: ${DAO.phonenumber} already exists")
                }
            }
            handle.createUpdate(
                "Insert Into $USER_TABLE" +
                        "(username, firstname , lastname, phonenumber, password, email) values" +
                        "(:username,:firstname,:lastname,:phonenumber,:password,:email)"
            )
                .bind("username", DAO.username)
                .bind("firstname", DAO.firstname)
                .bind("lastname", DAO.lastname)
                .bind("phonenumber", DAO.phonenumber)
                .bind("password", DAO.password)
                .bind("email", DAO.email)
                .execute()

            for (address in DAO.addresses) {
                handle.createUpdate(
                    "Insert Into $CLIENT_ADDRESSES_TABLE" +
                            "(clientusername, postal_code, address) values" +
                            "(:clientusername,:postal_code ,:address)"
                )
                    .bind("clientusername", DAO.username)
                    .bind("postal_code", address.postalCode)
                    .bind("address", address.address)
                    .execute()
            }
            return@inTransaction DAO.username
        }


    override fun read(key: String): Client? =
        jdbi.inTransaction<Client, Exception> { handle ->

            val clientOpt = handle.createQuery(
                "SELECT username, firstname , lastname, phonenumber, password, email " +
                        "from $USER_TABLE " +
                        "where username = :username"
            )
                .bind("username", key)
                .mapTo(Client::class.java)
                .findOne()

            if(clientOpt.isEmpty) return@inTransaction null


            val client = clientOpt.get()

            val collectionSize =
                handle.createQuery("SELECT count(*) from  $CLIENT_ADDRESSES_TABLE where clientUsername = :username")
                    .bind("username", key)
                    .mapTo(Int::class.java)
                    .first()

            if (collectionSize >= 0) {
                val addresses = handle.createQuery(
                    "SELECT clientUsername , latitude, longitude, postal_code, address " +
                            "from $CLIENT_ADDRESSES_TABLE " +
                            "where clientUsername = :username"
                ).bind("username", key).mapTo(Address::class.java).list()

                client.addresses = addresses
            }

            return@inTransaction client
        }

    fun readAll(): List<Client> =
        jdbi.inTransaction<List<Client>, Exception> { handle ->

            val clients = handle.createQuery(
                "SELECT * from $USER_TABLE "
            )
                .mapTo(Client::class.java)
                .list()

            //TODO: Improve code
            clients.forEach {
                val collectionSize =
                    handle.createQuery("SELECT count(*) from  $CLIENT_ADDRESSES_TABLE where clientUsername = :username")
                        .bind("username", it.username)
                        .mapTo(Int::class.java)
                        .first()

                if (collectionSize >= 0) {
                    val addresses = handle.createQuery(
                        "SELECT clientUsername, latitude, longitude, postal_code, address " +
                                "from $CLIENT_ADDRESSES_TABLE " +
                                "where clientUsername = :username"
                    ).bind("username", it.username).mapTo(Address::class.java).list()

                    it.addresses = addresses
                }
            }

            return@inTransaction clients
        }


    override fun update(DAO: Client) {
        TODO("Not yet implemented")
    }

    override fun delete(key: String) {
        jdbi.useTransaction<Exception> { handle ->

            handle.createUpdate(
                "DELETE from $CLIENT_ADDRESSES_TABLE" +
                        "where clientUsername = :username"
            )
                .bind("username", key)
                .execute()

            handle.createUpdate(
                "DELETE from $USER_TABLE" +
                        "where username = :username"
            )
                .bind("username", key)
                .execute()
        }

    }

    fun addAddress(address: Address) {
        jdbi.useHandle<Exception> { handle ->

            if (!userExists(address.clientUsername,handle))
                throw UserNotFoundException("The user: ${address.clientUsername} doesn't exist")

            handle.createUpdate(
                "Insert Into $CLIENT_ADDRESSES_TABLE" +
                        "(clientusername, postal_code, address) values" +
                        "(:clientusername,:postal_code ,:address)"
            )
                .bind("clientusername", address.clientUsername)
                .bind("postal_code", address.postalCode)
                .bind("address", address.address)
                .execute()
        }
    }

    fun removeAddress(username: String, addressId: Long) {
        jdbi.useHandle<Exception> { handle ->

            if (!userExists(username, handle))
                throw UserNotFoundException("The user: $username doesn't exist")
            if (!addressBelongsToUser(username, addressId, handle))
                throw UserNotAllowedException("The user: $username doesn't have permissions to this address")

            handle.createUpdate(
                "DELETE FROM $CLIENT_ADDRESSES_TABLE WHERE addressid = :addressId"
            )
                .bind("addressId", addressId)
                .execute()
        }
    }

    /*
    fun getAddress(username: String, addressId: Int): Address =
        jdbi.inTransaction<Address, Exception> { handle ->

            if (!userExists(username, handle)) throw UserNotFoundException("The user: $username doesn't exist")

            val optional = handle.createQuery("SELECT addressid, clientusername " +
                    "FROM $CLIENT_ADDRESSES_TABLE WHERE " +
                    "addressId = :addressId")
                .bind("addressId", addressId)
                .mapTo(ADDRESS_VERIFICATION::class.java)
                .findFirst()

            if(optional.isEmpty){
                throw UserNotFoundException("The user: $username doesn't exist")
            } else {
                val address = optional.get()
                if(address.username != username){
                    throw UserNotAllowedException("The user: $username doesn't have access to this address")
                }
            }

            val addressFound = handle.createQuery("SELECT addressid, clientusername " +
                    "from $CLIENT_ADDRESSES_TABLE where addressid = :id ")

                .bind("id", addressId)
                .bind("username", username)
                .mapTo(Address::class.java)
                .findOne()

            if (addressFound.isEmpty) throw AddressNotFoundException("The address: $addressId doesn't exist")


            return@inTransaction handle.createQuery(
                "SELECT * FROM $CLIENT_ADDRESSES_TABLE " +
                        "WHERE clientusername = :username AND addressid = :addressId"
            )
                .bind("username", username)
                .bind("addressId", addressId)
                .mapTo(Address::class.java)
                .one()
        }
        //NOT WORKING ATM
     */

    fun getAddresses(username: String): List<Address> =
        jdbi.inTransaction<List<Address>, Exception> { handle ->

            if (!userExists(username, handle)) throw UserNotFoundException("The user: $username doesn't exist")

            return@inTransaction handle.createQuery(
                "SELECT * FROM $CLIENT_ADDRESSES_TABLE " +
                        "WHERE clientusername = :username"
            )
                .bind("username", username)
                .mapTo(Address::class.java)
                .list()
        }

    fun giveRatingAndReward(username: String, deliveryId: Long, rating: Int, reward: Float) {
        jdbi.useHandle<Exception> { handle ->

            if(userExists(username, handle))
                throw UserNotFoundException("The user: $username doesn't exist")

            if(deliveryBelongsToUser(username, deliveryId, handle))

            handle.createUpdate(
                "UPDATE $DELIVERIES_TABLE SET rating = :rating, reward = :reward WHERE deliveryid = :deliveryId"
            )
                .bind("rating", rating)
                .bind("reward", reward)
                .bind("deliveryId", deliveryId)
                .execute()
        }
    }

    fun getUsernameByPhone(phoneNumber: String) : String? =
        jdbi.withHandle<String?, Exception> { handle ->

            val usernameOptional = handle.createQuery(
                "SELECT username FROM $USER_TABLE where phonenumber = :phonenumber"
            )
                .bind("phonenumber", phoneNumber)
                .mapTo(String::class.java)
                .findOne()

            if(usernameOptional.isEmpty) return@withHandle null

            return@withHandle usernameOptional.get()
        }

    //Auxiliary verification methods
    fun validateUser(username: String, password: String): Boolean =
        jdbi.withHandle<Boolean, Exception> { handle ->
            handle.createQuery("select count(*) FROM $USER_TABLE where username = :username AND password = :password")
                .bind("username", username)
                .bind("password", password)
                .mapTo(Int::class.java)
                .findFirst()
                .get() == 1
        }

    fun userExists(username: String, handle: Handle): Boolean {
        val clientFound = handle.createQuery("SELECT username from $USER_TABLE where username = :username")
            .bind("username", username)
            .mapTo(String::class.java)
            .findOne()

        return !clientFound.isEmpty
    }

    fun userDetailsExist(username: String, email: String, phoneNumber: String, handle: Handle){

    }

    fun emailExists(email: String, handle: Handle): Boolean {
        val emailFound = handle.createQuery("SELECT email from $USER_TABLE where email = :email")
            .bind("email", email)
            .mapTo(String::class.java)
            .findOne()

        return !emailFound.isEmpty
    }

    //TODO: Implement
    fun emailOrPhoneExists(email: String, phoneNumber: String, handle: Handle): Boolean {
        val emailFound = handle.createQuery("SELECT email, phonenumber from $USER_TABLE where email = :email OR phonenumber = :phonenumber")
            .bind("email", email)
            .bind("phonenumber", phoneNumber)
            .mapTo(String::class.java)
            .findOne()

        println(emailFound)

        return !emailFound.isEmpty
    }

    fun phoneExists(phoneNumber: String, handle: Handle): Boolean {
        val phoneFound = handle.createQuery("SELECT phonenumber from $USER_TABLE where phonenumber = :phoneNumber")
            .bind("phoneNumber", phoneNumber)
            .mapTo(String::class.java)
            .findOne()

        return !phoneFound.isEmpty
    }

    fun deliveryBelongsToUser(username: String, deliveryId: Long, handle: Handle): Boolean {

        val expectedUser = handle.createQuery("SELECT clientusername from $DELIVERIES_TABLE " +
                "WHERE deliveryid = :deliveryid")
            .bind("deliveryid", deliveryId)
            .mapTo(String::class.java)
            .findOne()

        return expectedUser.get() == username
    }

    fun addressBelongsToUser(username: String, addressId: Long, handle: Handle): Boolean {

        val expectedUser = handle.createQuery("SELECT clientusername from $CLIENT_ADDRESSES_TABLE " +
                "WHERE addressid = :addressid")
            .bind("addressid", addressId)
            .mapTo(String::class.java)
            .findOne()

        return expectedUser.get() == username
    }

    //Exceptions
    class UserNotFoundException(s: String) : Exception(s)
    class UserNotAllowedException(s: String) : Exception(s)
    class AddressNotFoundException(s: String) : Exception(s)
    class UserAlreadyExistsException(s: String) : Exception(s)
    class PhoneAlreadyExistsException(s: String) : Exception(s)
    class EmailAlreadyExistsException(s: String) : Exception(s)
}




