package com.isel.warpDelivery.dataAccess.mappers

import org.jdbi.v3.core.Jdbi


// K represents the type of Key , O Represents the type of Object
abstract class DataMapper <K,O>(protected val jdbi : Jdbi){
    abstract fun create(DAO : O) : K
    abstract fun read(key : K) : O
    abstract fun update(DAO : O)
    abstract fun delete(key : K)
}