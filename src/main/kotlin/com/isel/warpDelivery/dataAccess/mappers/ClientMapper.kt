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
    }

    override fun create(DAO: Client) : String =
        jdbi.inTransaction<String,Exception> { handle ->

            if (userExists(
                    DAO.username,
                    handle
                )
            ) throw UserAlreadyExistsException("The user ${DAO.username} already exists")

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
            handle.commit()
            return@inTransaction DAO.username
        }


    override fun read(key: String): Client =
        jdbi.inTransaction<Client, Exception> { handle ->

            if (!userExists(key, handle)) throw UserNotFoundException("The user: $key doesn't exist")

            val client = handle.createQuery(
                "SELECT username, firstname , lastname, phonenumber, password, email " +
                        "from $USER_TABLE " +
                        "where username = :username"
            )
                .bind("username", key)
                .mapTo(Client::class.java)
                .one()

            val collectionSize =
                handle.createQuery("SELECT count(*) from  $CLIENT_ADDRESSES_TABLE where clientUsername = :username")
                    .bind("username", key)
                    .mapTo(Int::class.java)
                    .first()

            if (collectionSize >= 0) {
                val addresses = handle.createQuery(
                    "SELECT clientUsername , postal_code, address " +
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
                        "SELECT clientUsername , postal_code, address " +
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

    fun removeAddress(username: String, addressId: Int) {
        jdbi.useHandle<Exception> { handle ->

            if (!userExists(username, handle)) throw UserNotFoundException("The user: $username doesn't exist")

            handle.createUpdate(
                "DELETE FROM $CLIENT_ADDRESSES_TABLE WHERE addressid = :addressId"
            )
                .bind("addressId", addressId)
                .execute()
        }
    }

    fun getAddress(username: String, addressId: Int): Address =
        jdbi.inTransaction<Address, Exception> { handle ->

            if (!userExists(username, handle)) throw UserNotFoundException("The user: $username doesn't exist")

            val addressFound = handle.createQuery("SELECT addressid from $CLIENT_ADDRESSES_TABLE where addressid = :id")
                .bind("id", addressId)
                .mapTo(Long::class.java)
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
            handle.createUpdate(
                "UPDATE $DELIVERIES_TABLE SET rating = :rating, reward = :reward WHERE deliveryid = :deliveryId"
            )
                .bind("rating", rating)
                .bind("reward", reward)
                .bind("deliveryId", deliveryId)
                .execute()
        }
    }

    fun validateUser(username: String, password: String): Boolean =
        jdbi.withHandle<Boolean, Exception> { handle ->
            handle.createQuery("select count(*) FROM $USER_TABLE where username = :username AND password = :password")
                .bind("username", username)
                .bind("password", password)
                .mapTo(Int::class.java)
                .findFirst()
                .get() == 1
        }

    class UserNotFoundException(s: String) : Exception(s)
    class AddressNotFoundException(s: String) : Exception(s)
    class UserAlreadyExistsException(s: String) : Exception(s)

    fun userExists(username: String, handle: Handle): Boolean {
        val clientFound = handle.createQuery("SELECT username from $USER_TABLE where username = :username")
            .bind("username", username)
            .mapTo(String::class.java)
            .findOne()

        return !clientFound.isEmpty
    }
}




