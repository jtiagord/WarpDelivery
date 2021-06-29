package com.isel.warpDelivery.errorHandling

import com.isel.warpDelivery.exceptionHandler.ProblemJsonModel
import org.apache.http.entity.ContentType
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler


class ApiException(val errorMessage : String,val statusCode: HttpStatus = HttpStatus.BAD_REQUEST) : Exception(errorMessage){
    companion object{
        class ErrorObject(val errorMessage : String)
    }
    fun getErrorObject() = ErrorObject(errorMessage)
}

