package dataAccess.mappers

import com.isel.warpDelivery.dataAccess.DAO.Store
import com.isel.warpDelivery.dataAccess.mappers.DataMapper
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

@Component
class StoreMapper(jdbi: Jdbi) : DataMapper<String, Store>(jdbi) {

    companion object {
        const val STORE_TABLE = "STORE"
    }

    override fun create(DAO: Store) {
        jdbi.useTransaction<Exception> { handle ->
            handle.createUpdate(
                "Insert Into $STORE_TABLE" +
                        "(name, postal_code, address) values" +
                        "(:name, :postalcode, :address,)"
            )
                .bind("name", DAO.name)
                .bind("postalcode", DAO.postalCode)
                .bind("address", DAO.address)
                .execute()

            handle.commit()
        }
    }

    override fun read(key: String): Store =
        jdbi.inTransaction<Store, Exception> { handle ->
            val store = handle.createQuery(
                "SELECT storeId, name, postal_code , address" +
                        "from $STORE_TABLE" +
                        "where storeId = :storeId"
            )
                .bind("storeId", key)
                .mapTo(Store::class.java).one()

            return@inTransaction store
        }

    override fun update(DAO: Store) {
        TODO("Not yet implemented")
    }

    override fun delete(key: String) {
        TODO("Not yet implemented")
    }
}