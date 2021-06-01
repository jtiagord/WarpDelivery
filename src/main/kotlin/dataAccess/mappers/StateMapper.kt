package dataAccess.mappers

import DataAccess.mappers.DataMapper
import dataAccess.DAO.Delivery
import dataAccess.DAO.Warper
import org.jdbi.v3.core.Jdbi

class StateMapper(jdbi: Jdbi) : DataMapper<String, Warper>(jdbi) {
    companion object {
        const val USER_TABLE = "USER"
    }
    override fun Create(DAO: Warper) {
        TODO("Not yet implemented")
    }

    override fun Read(key: String): Warper =
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

    override fun Update(DAO: Warper) {
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

    override fun Delete(key: String) {
        TODO("Not yet implemented")
    }
}