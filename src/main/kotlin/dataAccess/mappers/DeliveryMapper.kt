package DataAccess.mappers

import dataAccess.DAO.Delivery
import dataAccess.DAO.StateTransition
import dataAccess.DAO.Warper
import dataAccess.mappers.WarperMapper
import org.jdbi.v3.core.Jdbi

class DeliveryMapper(jdbi: Jdbi) : DataMapper<String, Delivery>(jdbi) {

    companion object {
        const val DELIVERY_TABLE = "DELIVERY"
        const val TRANSITIONS_TABLE = "STATE_TRANSITIONS"
    }

    override fun Create(DAO: Delivery) {
        jdbi.useTransaction<Exception> { handle ->

            handle.createUpdate(
                "Insert Into $DELIVERY_TABLE" +
                        "(deliveryid, state , purchasedate, deliverydate, rating, price, type) values" +
                        "(:id, :clientid, :warperid, :state, :purchasedate, :deliverydate, :rating, :price, :type)"
            )
                .bind("id", DAO.deliveryId)
                .bind("clientId", DAO.clientId)
                .bind("warperId", DAO.warperId)
                .bind("state", DAO.state)
                .bind("purchase_date", DAO.purchaseDate)
                .bind("delivery_date", DAO.deliveryDate)
                .bind("rating", DAO.rating)
                .bind("price", DAO.price)
                .bind("type", DAO.type)
                .execute()

            for (transition in DAO.transitions) {
                handle.createUpdate(
                    "Insert Into $TRANSITIONS_TABLE" +
                            "(deliveryId, transitiondate, previous_state, next_state) values" +
                            "(:id, :date, :prev_state, :next_state)"
                )
                    .bind("id", DAO.deliveryId)
                    .bind("date", transition.transitionDate)
                    .bind("prev_state", transition.previousState)
                    .bind("next_state", transition.nextState)
                    .execute()
            }

            handle.commit()
        }
    }

    override fun Read(key: String): Delivery =
        jdbi.inTransaction<Delivery, Exception> { handle ->
            val delivery = handle.createQuery(
                "SELECT deliveryid, clientid, warperid, " +
                        "state, purchasedate, deliverydate, rating, price, type " +
                        "from $DELIVERY_TABLE " +
                        "where deliveryid = :id"
            )
                .bind("id", key)
                .mapTo(Delivery::class.java)
                .one()

            val transitions = handle.createQuery(
                "SELECT  deliveryid, transitiondate, previousstate, nextstate " +
                        "from $TRANSITIONS_TABLE " +
                        "where deliveryid = :id"
            ).bind("id", key).mapTo(StateTransition::class.java).list()

            delivery.transitions = transitions

            return@inTransaction delivery
        }

    override fun Update(DAO: Delivery) {
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
                .bind("deliverydate", DAO.deliveryDate)
                .bind("rating", DAO.rating)
                .execute()
        }
    }

    override fun Delete(key: String) {
        jdbi.useTransaction<Exception> { handle ->
            handle.createUpdate(
                "DELETE from $DELIVERY_TABLE " +
                        "where deliveryid = :deliveryid"
            )
                .bind("deliveryid", key)
                .execute()
        }
    }

    fun readAll(key: String): List<Delivery> =
        jdbi.inTransaction<List<Delivery>, Exception> { handle ->
            return@inTransaction handle.createQuery(
                "SELECT deliveryid,clientusername,warperusername,state " +
                        "from $DELIVERY_TABLE " +
                        "where deliveryid=:id"
            )
                .bind("id",key)
                .mapTo(Delivery::class.java)
                .list()
        }
}

