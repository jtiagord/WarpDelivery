package com.isel.warpDelivery.dataAccess.mappers

/*
@Component
class TransitionMapper(jdbi: Jdbi) : DataMapper<List<String>, StateTransition>(jdbi) {

    companion object {
        const val STATE_TRANSITIONS_TABLE = "STATE_TRANSITIONS"
    }

    override fun read(key: List<String>): StateTransition =
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


    override fun create(DAO: StateTransition) : {
        TODO("Not yet implemented")
    }

    override fun delete(key: List<String>) {
        TODO("Not yet implemented")
    }

    override fun update(DAO: StateTransition) {
        TODO("Not yet implemented")
    }
}*/