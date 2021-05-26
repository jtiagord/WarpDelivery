package DataAccess.mappers

import org.jdbi.v3.core.Jdbi


// K represents the type of Key , O Represents the type of Object
abstract class DataMapper <K,O>(protected val jdbi : Jdbi){
    abstract fun Create(DAO : O)
    abstract fun Read(key : K) : O
    abstract fun Update(DAO : O)
    abstract fun Delete(key : K)
}