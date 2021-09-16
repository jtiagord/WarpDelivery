package com.isel.warpDelivery.errorHandling

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ErrorHandlingController {
    @ExceptionHandler(ApiException::class)
    fun handleTransitionException(ex: ApiException)=
         ResponseEntity
            .status(ex.statusCode)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                ex.getErrorObject()
            )

}