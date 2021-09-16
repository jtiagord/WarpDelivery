package com.isel.warpDelivery.errorHandling


import org.springframework.http.HttpStatus



class ApiException(val errorMessage : String,val statusCode: HttpStatus = HttpStatus.BAD_REQUEST) : Exception(errorMessage){
    companion object{
        class ErrorObject(val errorMessage : String)
    }
    fun getErrorObject() = ErrorObject(errorMessage)
}

