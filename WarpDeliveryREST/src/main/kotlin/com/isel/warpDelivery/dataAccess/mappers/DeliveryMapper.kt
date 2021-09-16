package com.isel.warpDelivery.dataAccess.mappers

import com.isel.warpDelivery.authentication.UserInfo
import com.isel.warpDelivery.dataAccess.dataClasses.Delivery
import com.isel.warpDelivery.dataAccess.dataClasses.DeliveryFullInfo
import com.isel.warpDelivery.dataAccess.dataClasses.DeliveryState
import com.isel.warpDelivery.dataAccess.dataClasses.StateTransition
import com.isel.warpDelivery.errorHandling.ApiException
import org.jdbi.v3.core.Jdbi
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component


@Component
class DeliveryMapper(val jdbi: Jdbi) {

    companion object {
        const val DELIVERY_TABLE = "DELIVERY"
        const val WARPER_TABLE = "WARPER"
        const val STORE_TABLE = "STORE"
        const val TRANSITIONS_TABLE = "STATE_TRANSITIONS"
    }

    fun create(DAO: Delivery) : String=
        jdbi.withHandle<String,Exception> { handle ->

            val delivery = handle.createUpdate(
                "INSERT INTO DELIVERY (warperusername, clientphone, storeid, state, " +
                        "deliverLatitude, deliverLongitude, deliverAddress, type) " +
                        "VALUES " +
                        "(:warperUsername, :clientphone, :storeId, :state, "+
                        " :deliverLatitude, :deliverLongitude, :deliverAddress, :type)"
            )
            .bind("warperUsername", DAO.warperUsername)
            .bind("clientphone", DAO.clientPhone)
            .bind("storeId", DAO.storeId)
            .bind("state", DAO.state.text)
            .bind("deliverLatitude", DAO.deliverLatitude)
            .bind("deliverLongitude", DAO.deliverLongitude)
            .bind("deliverAddress", DAO.deliverAddress)
            .bind("type", DAO.type.text)
            .executeAndReturnGeneratedKeys()
            .mapTo(Delivery::class.java)
            .one()

            return@withHandle delivery.deliveryId
        }


    fun read(key: String): Delivery? =
        jdbi.inTransaction<Delivery, Exception> { handle ->
            val deliveryOpt = handle.createQuery(
                "SELECT deliveryid, warperusername, storeid, state, clientphone, purchasedate, " +
                        "deliverDate, deliverLatitude, deliverLongitude, deliverAddress, type " +
                        "from $DELIVERY_TABLE " +
                        "where deliveryid = :id"
            )
            .bind("id", key)
            .mapTo(Delivery::class.java)
            .findOne()

            if(deliveryOpt.isEmpty) return@inTransaction null

            val delivery = deliveryOpt.get()

            return@inTransaction delivery
        }

    fun update(DAO: Delivery) {
        jdbi.useTransaction<Exception> { handle ->
            handle.createUpdate(
                "update $DELIVERY_TABLE " +
                        "set state=:state, " +
                        "deliverydate=:deliverydate, " +
                        "rating=:rating" +
                        "where deliveryid=:deliveryid"
            )
            .bind("deliveryid", DAO.deliveryId)
            .bind("state", DAO.state)
            .bind("deliverydate", DAO.deliverDate)
            .execute()
        }
    }

    fun delete(key: String) {
        jdbi.useTransaction<Exception> { handle ->
            handle.createUpdate(
                "DELETE from $DELIVERY_TABLE " +
                        "where deliveryid = :deliveryid"
            )
            .bind("deliveryid", key)
            .execute()
        }
    }

    fun readAll(limit : Int, offset : Int): List<Delivery> =
        jdbi.inTransaction<List<Delivery>, Exception> { handle ->
            return@inTransaction handle.createQuery(
                "SELECT deliveryid, warperusername, storeid, state, clientphone, purchasedate, " +
                        "deliverDate, deliverLatitude, deliverLongitude, deliverAddress, rating, reward, type " +
                        "from $DELIVERY_TABLE " +

                        "limit  :limit " +
                        "offset :offset"
            )
            .bind("limit", limit)
            .bind("offset", offset)
            .mapTo(Delivery::class.java)
            .list()
        }

    fun updateState(key: String, state: DeliveryState) =
        jdbi.useTransaction<Exception> { handle ->

            val deliveryOpt = handle.createQuery(
                "SELECT deliveryid " +
                        "from $DELIVERY_TABLE " +
                        "where deliveryid = :id"
            )
            .bind("id", key)
            .mapTo(String::class.java)
            .findOne()


            if(deliveryOpt.isEmpty) throw ApiException("The delivery: $key doesn't exist", HttpStatus.NOT_FOUND)

            handle.createUpdate(
                "UPDATE $DELIVERY_TABLE " +
                        "SET state = :state " +
                        "WHERE deliveryid = :deliveryid"
            )
            .bind("state", state.text)
            .bind("deliveryid", key)
            .execute()
        }

    fun updateStateAndAssignWarper(key: String, state: DeliveryState, warperUsername : String) =
        jdbi.useTransaction<Exception> { handle ->

            val deliveryOpt = handle.createQuery(
                "SELECT deliveryid " +
                        "from $DELIVERY_TABLE " +
                        "where deliveryid = :id"
            )
                .bind("id", key)
                .mapTo(String::class.java)
                .findOne()


            if(deliveryOpt.isEmpty) throw ApiException("The delivery: $key doesn't exist", HttpStatus.NOT_FOUND)

            handle.createUpdate(
                "UPDATE $DELIVERY_TABLE " +
                        "SET state = :state, " +
                        "warperusername = :warperusername "+
                        "WHERE deliveryid = :deliveryid"
            )
                .bind("state", state.text)
                .bind("warperusername", warperUsername)
                .bind("deliveryid", key)
                .execute()
        }



    fun getDeliveriesByWarperUsername(username: String, limit : Int , offset : Int): List<DeliveryFullInfo> =

        jdbi.inTransaction<List<DeliveryFullInfo>, Exception> { handle ->
            return@inTransaction handle.createQuery(
                "SELECT * FROM $DELIVERY_TABLE  d " +
                        "JOIN $WARPER_TABLE w ON d.warperusername = w.username " +
                        "JOIN $STORE_TABLE s ON d.storeid = s.storeid " +
                        "WHERE d.warperusername = :warperusername " +
                        "ORDER BY purchasedate DESC"
                )
                .bind("limit", limit)
                .bind("offset", offset)
                .bind("warperusername", username)
                .mapTo(DeliveryFullInfo::class.java)
                .list()
        }

    fun getDeliveriesByStoreId(store: String, limit : Int , offset : Int): List<Delivery> =

        jdbi.inTransaction<List<Delivery>, Exception> { handle ->

            return@inTransaction handle.createQuery(
                "SELECT * FROM $DELIVERY_TABLE WHERE storeid = :store " +
                        "limit  :limit " +
                        "offset :offset"
            )
            .bind("limit", limit)
            .bind("offset", offset)
            .bind("store", store)
            .mapTo(Delivery::class.java)
            .list()
        }


    fun getTransitions(deliveryId: String): List<StateTransition> =
        jdbi.inTransaction<List<StateTransition> ,Exception> { handle ->

            return@inTransaction handle.createQuery(
                "SELECT transitiondate, previousstate, nextstate FROM $TRANSITIONS_TABLE " +
                        "WHERE deliveryid = :deliveryId"
            )
                .bind("deliveryId", deliveryId)
                .mapTo(StateTransition::class.java)
                .list()
        }

    fun verifyWarper(userInfo: UserInfo, deliveryId: String): Boolean =
        jdbi.withHandle<String, Exception> { handle ->
            val deliveryExists = handle.createQuery("select count(*) FROM $DELIVERY_TABLE where deliveryid = :id")
                .bind("id", deliveryId)
                .mapTo(Int::class.java)
                .findFirst()
                .get() == 1

            if(!deliveryExists)
                throw NoSuchElementException("Delivery $deliveryId does not exist")

            handle.createQuery("select warperusername from $DELIVERY_TABLE where deliveryid = :id")
                .bind("id", deliveryId)
                .mapTo(String::class.java)
                .findFirst()
                .get()
        }.compareTo(userInfo.id) == 0

    fun verifyClient(userInfo: UserInfo, deliveryId: String): Boolean =
        jdbi.withHandle<String, Exception> { handle ->
            val deliveryExists = handle.createQuery("select count(*) FROM $DELIVERY_TABLE where deliveryid = :id")
                .bind("id", deliveryId)
                .mapTo(Int::class.java)
                .findFirst()
                .get() == 1

            if(!deliveryExists)
                throw NoSuchElementException("Delivery $deliveryId does not exist")

            handle.createQuery("select clientusername from $DELIVERY_TABLE where deliveryid = :id")
                .bind("id", deliveryId)
                .mapTo(String::class.java)
                .findFirst()
                .get()
        }.compareTo(userInfo.id) == 0

    fun getDeliveryFullInfo(deliveryId: String): DeliveryFullInfo? =
        jdbi.withHandle<DeliveryFullInfo, Exception> { handle ->

            val result = handle.createQuery("SELECT * FROM $DELIVERY_TABLE  d" +
                        " LEFT JOIN $WARPER_TABLE w ON d.warperusername = w.username " +
                        "JOIN $STORE_TABLE s ON d.storeid = s.storeid " +
                        " WHERE d.deliveryid = :deliveryid")
                .bind("deliveryid",deliveryId)
                .mapTo(DeliveryFullInfo::class.java)
                .findOne()



            return@withHandle if(result.isPresent) result.get() else null
        }



    //Exceptions
    class DeliveryNotFoundException(s: String) : Exception(s)

}

