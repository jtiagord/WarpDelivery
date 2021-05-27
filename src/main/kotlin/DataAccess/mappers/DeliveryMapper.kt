package DataAccess.mappers

import DataAccess.DAO.Delivery
import DataAccess.DAO.StateTransition
import DataAccess.DAO.Vehicle
import DataAccess.DAO.Warper
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
                        "(deliveryId, state , purchase_date, delivery_date, rating, price, type) values" +
                        "(:id, :clientId, :warperId, :state, :purchase_date, :delivery_date, :rating, :price, :type)"
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
                "SELECT deliveryId, clientId, warperId, state, purchase_date, delivery_date, rating, price, type" +
                        "from $DELIVERY_TABLE" +
                        "where deliveryId = :id"
            )
                .bind("id", key)
                .mapTo(Delivery::class.java)
                .one()

            val transitions = handle.createQuery(
                "SELECT  deliveryId, transitiondate, previous_state, next_state" +
                        "from $TRANSITIONS_TABLE" +
                        "where deliveryId = :id"
            ).bind("id", key).mapTo(StateTransition::class.java).list()

            delivery.transitions = transitions

            return@inTransaction delivery
        }

    override fun Update(DAO: Delivery) {
        TODO("Not yet implemented")
    }

    override fun Delete(key: String) {
        TODO("Not yet implemented")
    }
}
