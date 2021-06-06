package com.isel.warpDelivery.dataAccess.mappers

import com.isel.warpDelivery.dataAccess.DAO.Address
import com.isel.warpDelivery.dataAccess.DAO.Client
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

@Component
class ClientMapper(jdbi : Jdbi) : DataMapper<String, Client>(jdbi) {
    companion object{
        const val USER_TABLE = "USERS"
        const val CLIENT_ADDRESSES_TABLE = "CLIENT_ADDRESS"
        const val DELIVERIES_TABLE = "DELIVERY"
    }
    override fun create(DAO: Client) {
        jdbi.useTransaction<Exception> { handle ->
            handle.createUpdate("Insert Into $USER_TABLE" +
                    "(username, firstname , lastname, phonenumber, password, email) values" +
                    "(:username,:firstname,:lastname,:phonenumber,:password,:email)")
                .bind("username"    , DAO.username)
                .bind("firstname"   , DAO.firstname)
                .bind("lastname"    , DAO.lastname)
                .bind("phonenumber" , DAO.phonenumber)
                .bind("password"    , DAO.password)
                .bind("email"       , DAO.email)
                .execute()

            for(address in DAO.addresses ){
                handle.createUpdate("Insert Into $CLIENT_ADDRESSES_TABLE" +
                        "(clientusername, postal_code, address) values" +
                        "(:clientusername,:postal_code ,:address)")
                    .bind("clientusername"    , DAO.username)
                    .bind("postal_code"   ,     address.postalCode)
                    .bind("address"       ,      address.address)
                    .execute()
            }
        }
    }

    override fun read(key: String): Client =
        jdbi.inTransaction<Client ,Exception> { handle ->
            val client = handle.createQuery(
                "SELECT username, firstname , lastname, phonenumber, password, email " +
                        "from $USER_TABLE " +
                        "where username = :username"
            )
            .bind("username", key)
            .mapTo(Client::class.java)
            .one()

            val addresses = handle.createQuery("SELECT clientUsername , postal_code, address " +
                    "from $CLIENT_ADDRESSES_TABLE " +
                    "where clientUsername = :username"
            ).bind("username", key).mapTo(Address::class.java).list()

            client.addresses = addresses

            return@inTransaction client
        }

    fun readAll(): List<Client> =
        jdbi.inTransaction<List<Client>, Exception> { handle ->
            return@inTransaction handle.createQuery(
                "SELECT * from $USER_TABLE "
            )
                .mapTo(Client::class.java)
                .list()
        }


    override fun update(DAO: Client) {
        TODO("Not yet implemented")
    }

    override fun delete(key: String) {
        jdbi.useTransaction<Exception> { handle ->
            handle.createUpdate("DELETE from $USER_TABLE" +
                        "where username = :username"
            )
            .bind("username" , key)
            .execute()

            handle.createUpdate("DELETE from $CLIENT_ADDRESSES_TABLE" +
                    "where clientUsername = :username"
            )
            .bind("username" , key)
            .execute()
        }

    }

    fun addAddress(address : Address){
        jdbi.useHandle<Exception> { handle ->
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
            handle.createUpdate(
                "DELETE FROM $CLIENT_ADDRESSES_TABLE WHERE addressid = :addressId")
                .bind("addressId", addressId)
                .execute()
        }
    }

    fun getAddress(username: String, addressId: Int) : Address =
        jdbi.inTransaction<Address ,Exception> { handle ->

            return@inTransaction handle.createQuery(
                "SELECT * FROM $CLIENT_ADDRESSES_TABLE " +
                        "WHERE clientusername = :username AND addressid = :addressId"
            )
                .bind("username", username)
                .bind("addressId", addressId)
                .mapTo(Address::class.java)
                .one()
        }

    fun getAddresses(username: String) : List<Address> =
        jdbi.inTransaction<List<Address> ,Exception> { handle ->

            return@inTransaction handle.createQuery(
                "SELECT * FROM $CLIENT_ADDRESSES_TABLE " +
                        "WHERE clientusername = :username"
            )
                .bind("username", username)
                .mapTo(Address::class.java)
                .list()
        }

    fun giveRatingAndReward(username: String, deliveryId: Int, rating: Int, reward: Float) {
        jdbi.useHandle<Exception> { handle ->
            handle.createUpdate(
                "UPDATE $DELIVERIES_TABLE SET rating = :rating, reward = :reward WHERE deliveryid = :deliveryId")
                .bind("rating", rating)
                .bind("reward", reward)
                .bind("deliveryId", deliveryId)
                .execute()
        }
    }
}


