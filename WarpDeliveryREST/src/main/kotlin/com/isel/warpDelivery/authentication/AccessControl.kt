package com.isel.warpDelivery.authentication

import com.isel.warpDelivery.exceptionHandler.UnauthorizedException
import org.slf4j.LoggerFactory
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Target(AnnotationTarget.FUNCTION)
annotation class WarperResource

@Target(AnnotationTarget.FUNCTION)
annotation class StoreResource

@Target(AnnotationTarget.FUNCTION)
annotation class AdminResource


class AccessControlInterceptor : HandlerInterceptor {

    val logger = LoggerFactory.getLogger(AccessControlInterceptor::class.java)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if(handler !is HandlerMethod) return true
        val userInfo = request.getAttribute(USER_ATTRIBUTE_KEY) as? UserInfo

        val isWarperResource = handler.hasMethodAnnotation(WarperResource::class.java)
        val isStoreResource = handler.hasMethodAnnotation(StoreResource::class.java)
        val isAdminResource = handler.hasMethodAnnotation(AdminResource::class.java)

        return when {
            isAdminResource && userInfo?.type == USERTYPE.ADMIN -> true
            isWarperResource && userInfo?.type == USERTYPE.WARPER -> true
            isStoreResource && userInfo?.type == USERTYPE.STORE -> true
            !isWarperResource && !isStoreResource && !isAdminResource-> true
            else -> {
                throw UnauthorizedException()
            }
        }
    }
}