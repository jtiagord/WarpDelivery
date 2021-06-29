package com.isel.warpDelivery.dataAccess.mappers

import com.isel.warpDelivery.dataAccess.DAO.Store
import com.isel.warpDelivery.errorHandling.ApiException
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

@Component
class StoreMapper(jdbi: Jdbi) : DataMapper<Long, Store>(jdbi) {

    companion object {
        const val STORE_TABLE = "STORE"
    }

    override fun create(DAO: Store): Long =
        jdbi.inTransaction<Long,Exception> { handle ->
            val store = handle.createUpdate(
                "Insert Into $STORE_TABLE" +
                        "(name, postalcode, address,latitude,longitude) values" +
                        "(:name, :postalcode, :address,:latitude,:longitude)"
            )
            .bind("name", DAO.name)
            .bind("postalcode", DAO.postalcode)
            .bind("address", DAO.address)
            .bind("latitude", DAO.latitude)
            .bind("longitude", DAO.longitude)
            .executeAndReturnGeneratedKeys()
            .mapTo(Store::class.java)
            .one()

            return@inTransaction store.storeId
        }


    override fun read(key: Long): Store? =
        jdbi.inTransaction<Store, Exception> { handle ->
            val store = handle.createQuery(
                "SELECT storeid, name, postalcode , address, latitude, longitude " +
                        "from $STORE_TABLE " +
                        "where storeid = :storeId"
            )
                .bind("storeId", key)
                .mapTo(Store::class.java).findOne()

            return@inTransaction if(store.isPresent) store.get() else null
        }

    override fun update(DAO: Store) {
        TODO("Not yet implemented")
    }

    override fun delete(key: Long) {
        TODO("Not yet implemented")
    }
}