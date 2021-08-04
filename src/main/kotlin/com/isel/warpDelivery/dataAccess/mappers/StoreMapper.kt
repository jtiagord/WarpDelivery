package com.isel.warpDelivery.dataAccess.mappers

import com.isel.warpDelivery.dataAccess.dataClasses.Store
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

@Component
class StoreMapper(val jdbi: Jdbi){

    companion object {
        const val STORE_TABLE = "STORE"
    }

    fun create(DAO: Store): String =
        jdbi.inTransaction<String,Exception> { handle ->
            val store = handle.createUpdate(
                "Insert Into $STORE_TABLE" +
                        "(name, postalcode, address,latitude,longitude, apiKey) values" +
                        "(:name, :postalcode, :address,:latitude,:longitude, :apiKey)"
            )
            .bind("name", DAO.name)
            .bind("postalcode", DAO.postalcode)
            .bind("address", DAO.address)
            .bind("latitude", DAO.latitude)
            .bind("longitude", DAO.longitude)
            .bind("apiKey", DAO.ApiKey)
            .executeAndReturnGeneratedKeys()
            .mapTo(Store::class.java)
            .one()

            return@inTransaction store.storeId
    }


    fun read(key: String): Store? =
        jdbi.inTransaction<Store, Exception> { handle ->
            val store = handle.createQuery(
                "SELECT * " +
                        "from $STORE_TABLE " +
                        "where storeid = :storeId"
            )
            .bind("storeId", key)
            .mapTo(Store::class.java).findOne()

            return@inTransaction if(store.isPresent) store.get() else null
        }

    fun update(DAO: Store) {
        TODO("Not yet implemented")
    }

    fun delete(key: String) {
        TODO("Not yet implemented")
    }


    fun getStoreByApiKey(apiKey : String) : Store? =
        jdbi.withHandle<Store, Exception>{ handle ->
            val store = handle.createQuery(
                "SELECT * " +
                    "from $STORE_TABLE " +
                    "where apiKey = :apiKey"
            )
            .bind("apiKey",apiKey)
            .mapTo(Store::class.java)
            .findFirst()

            return@withHandle if(store.isEmpty) null else store.get()
        }

}