package DataAccess.mappers

import DataAccess.DAO.Address
import DataAccess.DAO.Client
import org.jdbi.v3.core.Jdbi

class ClientMapper(jdbi : Jdbi) : DataMapper<String, Client>(jdbi) {
    companion object{
        const val USER_TABLE = "USERS"
        const val CLIENT_ADDRESSES_TABLE = "CLIENT_ADDRESS"
    }
    override fun Create(DAO: Client) {
        jdbi.useTransaction<Exception> { handle ->
            handle.createUpdate("Insert Into $USER_TABLE" +
                    "(username, firstname , lastname, phonenumber, password, email) values" +
                    "(:username,:firstname,:lastname,:phonenumber,:password,:email)")
                .bind("username"    , DAO.username)
                .bind("firstname"   , DAO.firstname)
                .bind("lastname"    , DAO.lastname)
                .bind("phonenumber" , DAO.lastname)
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

            handle.commit()
        }
    }

    override fun Read(key: String): Client {
        TODO("Not yet implemented")
    }

    override fun Update(DAO: Client) {
        TODO("Not yet implemented")
    }

    override fun Delete(key: String) {
        TODO("Not yet implemented")
    }

    fun AddAddress(key : String, address : Address){
        jdbi.useHandle<Exception> { handle ->
            handle.createUpdate(
                "Insert Into $CLIENT_ADDRESSES_TABLE" +
                        "(clientusername, postal_code, address) values" +
                        "(:clientusername,:postal_code ,:address)"
            )
                .bind("clientusername", key)
                .bind("postal_code", address.postalCode)
                .bind("address", address.address)
                .execute()
        }
    }
}