package com.isel.warpDelivery.exceptionHandler

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UnauthorizedException : Exception()

@ResponseStatus(HttpStatus.FORBIDDEN)
class ForbiddenException : Exception()