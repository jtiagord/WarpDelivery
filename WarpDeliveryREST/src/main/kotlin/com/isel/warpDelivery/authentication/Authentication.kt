package com.isel.warpDelivery.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.isel.warpDelivery.common.ISSUER
import com.isel.warpDelivery.common.KeyPair
import com.isel.warpDelivery.dataAccess.mappers.StoreMapper
import org.apache.catalina.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

enum class USERTYPE{
    WARPER,CLIENT,STORE,ADMIN
}
open class UserInfo(val id: String, val type : USERTYPE)

const val USER_ATTRIBUTE_KEY = "user-attribute"
const val BEARER_SCHEME = "BEARER"
const val KEY_SCHEME = "KEY="

@Component
class AuthenticationFilter(val keys : KeyPair, val storeMapper : StoreMapper) : Filter {

    var logger: Logger = LoggerFactory.getLogger(AuthenticationFilter::class.java)

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {

        val httpRequest = request as HttpServletRequest
        val authorizationHeader = httpRequest.getHeader("authorization") ?: null

        if(authorizationHeader!=null) {
            val trimmedHeader = authorizationHeader.trim()
            val userInfo : UserInfo?
            if(trimmedHeader.startsWith(BEARER_SCHEME, ignoreCase = true)) {
                userInfo = verifyUserCredentials(trimmedHeader.drop(BEARER_SCHEME.length).trim())

                if (userInfo != null) {
                    httpRequest.setAttribute(USER_ATTRIBUTE_KEY, userInfo)
                    logger.info("LOGGED IN AS ${userInfo.id}")
                }
            }else if(trimmedHeader.startsWith(KEY_SCHEME, ignoreCase = true)){
                userInfo = verifyStoreCredentials(trimmedHeader.drop(KEY_SCHEME.length).trim())

                if (userInfo != null) {
                    httpRequest.setAttribute(USER_ATTRIBUTE_KEY, userInfo)
                    logger.info("LOGGED IN AS ${userInfo.type} with id ${userInfo.id}")
                }
            }
        }


        chain?.doFilter(request, response)
    }

    private fun verifyStoreCredentials(key: String): UserInfo? {
        val store = storeMapper.getStoreByApiKey(key) ?: return null
        return UserInfo(store.storeId!!, USERTYPE.STORE)
    }

    fun verifyUserCredentials(token: String): UserInfo? {

        val algorithm = Algorithm.RSA256(keys.publicKey, keys.privateKey)
        val verifier: JWTVerifier = JWT.require(algorithm)
            .withIssuer(ISSUER)
            .acceptLeeway(5L*60) //Accept a leeway of 5 minutes
            .build()

        val jwt : DecodedJWT

        try {
            jwt = verifier.verify(token)
        } catch (ex: Exception) {
            when(ex) {
                is JWTDecodeException, is JWTVerificationException -> {
                    logger.info("Verifying user exception : ${ex.message}")
                    return null
                }
                else -> throw ex
            }
        }
        val userType = USERTYPE.valueOf(jwt.claims["usertype"]?.asString()?: "CLIENT")
        val id = jwt.claims["id"]?.asString()?: return null
        return UserInfo(id,userType)
    }

}