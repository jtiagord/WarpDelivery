package com.isel.warpDelivery.authentication

import com.isel.warpDelivery.UnauthorizedException
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Target(AnnotationTarget.FUNCTION)
annotation class ProtectedResource


class AccessControlInterceptor : HandlerInterceptor {

    private enum class AccessLevel { PROTECTED, PUBLIC }

    private fun getAccessLevel(handlerMethod: HandlerMethod?): AccessLevel {
        val isProtected: Boolean = handlerMethod?.hasMethodAnnotation(ProtectedResource::class.java) ?: false

        return when {
            handlerMethod == null -> AccessLevel.PUBLIC
            isProtected -> AccessLevel.PROTECTED
            else -> AccessLevel.PUBLIC
        }
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val routeHandler = handler as? HandlerMethod
        val userInfo = request.getAttribute(USER_ATTRIBUTE_KEY) as? UserInfo

        val accessLevel = getAccessLevel(routeHandler)
        return when {
            accessLevel == AccessLevel.PUBLIC -> true
            accessLevel == AccessLevel.PROTECTED && userInfo != null -> true
            else -> {
                throw UnauthorizedException()
            }
        }
    }
}