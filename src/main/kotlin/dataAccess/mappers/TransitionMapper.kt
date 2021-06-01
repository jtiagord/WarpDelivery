package dataAccess.mappers

import DataAccess.mappers.DataMapper
import DataAccess.mappers.DeliveryMapper
import dataAccess.DAO.Delivery
import dataAccess.DAO.StateTransition
import org.jdbi.v3.core.Jdbi

class TransitionMapper(jdbi: Jdbi) : DataMapper<List<String>, StateTransition>(jdbi) {

    companion object {
        const val STATE_TRANSITIONS_TABLE = "STATE_TRANSITIONS"
    }

    override fun Read(key: List<String>): StateTransition =
        jdbi.inTransaction<StateTransition, Exception> { handle ->
            return@inTransaction handle.createQuery(
                "SELECT * " +
                        "from $STATE_TRANSITIONS_TABLE " +
                        "where username = :username " +
                        "and transitiondate=:transitiondate"
            )
                .bind("username", key[0])
                .bind("transitiondate", key[1])
                .mapTo(StateTransition::class.java)
                .one()
        }

    fun readAll(username:String): List<StateTransition> =
        jdbi.inTransaction<List<StateTransition>, Exception> { handle ->
            return@inTransaction handle.createQuery(
                "SELECT * " +
                        "from $STATE_TRANSITIONS_TABLE " +
                        "where username = :username " +
                        "and transitiondate=:transitiondate"
            )
                .bind("username", username)
                .mapTo(StateTransition::class.java)
                .list()
        }


    override fun Create(DAO: StateTransition) {
        TODO("Not yet implemented")
    }

    override fun Delete(key: List<String>) {
        TODO("Not yet implemented")
    }

    override fun Update(DAO: StateTransition) {
        TODO("Not yet implemented")
    }
}