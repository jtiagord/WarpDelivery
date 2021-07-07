
package com.isel.warpDelivery.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.isel.warpDelivery.common.ISSUER
import com.isel.warpDelivery.common.KeyPair
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

enum class USERTYPE{
    WARPER,CLIENT,STORE
}
open class UserInfo(val id: String, val type : USERTYPE)

const val USER_ATTRIBUTE_KEY = "user-attribute"
const val BEARER_SCHEME = "BEARER"

@Component
class AuthenticationFilter(val keys : KeyPair) : Filter {

    var logger: Logger = LoggerFactory.getLogger(AuthenticationFilter::class.java)

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {

        val httpRequest = request as HttpServletRequest
        val authorizationHeader = httpRequest.getHeader("authorization") ?: ""

        val userInfo = verifyUserCredentials(authorizationHeader)
        if(userInfo != null) {
            httpRequest.setAttribute(USER_ATTRIBUTE_KEY, userInfo)
            logger.info("LOGGED IN AS ${userInfo.id}")
        }

        chain?.doFilter(request, response)

    }

    fun verifyUserCredentials(response: String): UserInfo? {
        val trimmedResponse = response.trim()
        if(trimmedResponse.startsWith(BEARER_SCHEME, ignoreCase = true)) {
            val token = trimmedResponse.drop(BEARER_SCHEME.length + 1).trim()

            val algorithm = Algorithm.RSA256(keys.publicKey, keys.privateKey)
            val verifier: JWTVerifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build()

            val jwt : DecodedJWT
            try {
                jwt = verifier.verify(token)
            } catch (ex: Exception) {
                when(ex) {
                    is JWTDecodeException, is JWTVerificationException -> {
                        return null
                    }
                    else -> throw ex
                }
            }

            val userType = USERTYPE.valueOf(jwt.claims["usertype"]?.asString()?: "CLIENT")
            val id = jwt.claims["id"]?.asString()?: return null
            return UserInfo(id,userType)
        }
        return null
    }
}