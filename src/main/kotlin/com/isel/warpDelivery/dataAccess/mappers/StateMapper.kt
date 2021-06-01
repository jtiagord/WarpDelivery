package com.isel.warpDelivery.dataAccess.mappers

import com.isel.warpDelivery.dataAccess.DAO.Warper
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

@Component
class StateMapper(jdbi: Jdbi) : DataMapper<String, Warper>(jdbi) {
    companion object {
        const val USER_TABLE = "USER"
    }
    override fun create(DAO: Warper) {
        TODO("Not yet implemented")
    }

    override fun read(key: String): Warper =
        jdbi.inTransaction<Warper, Exception> { handle ->
            return@inTransaction handle.createQuery(
                "SELECT state " +
                        "from $USER_TABLE " +
                        "where username = :username"
            )
                .bind("username", key)
                .mapTo(Warper::class.java)
                .one()
        }

    override fun update(DAO: Warper) {
        jdbi.useTransaction<Exception> { handle ->
            handle.createUpdate(
                "update $USER_TABLE} " +
                        "set state=:state "+
                        "where username=:username"
            )
                .bind("username", DAO.username)
                .bind("state", DAO.state)
                .execute()
        }
    }

    override fun delete(key: String) {
        TODO("Not yet implemented")
    }
}