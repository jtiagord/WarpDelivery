package com.isel.warpDelivery.common

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom


fun encodePassword(password : String) : String{

    val saltBytes = ByteArray(16)
    val secureRandom = SecureRandom()
    secureRandom.nextBytes(saltBytes)

    val digest = MessageDigest.getInstance("SHA-256")
    val passwordBytes = password.toByteArray(StandardCharsets.UTF_8)

    val hashedPassword = digest.digest(
        saltBytes+passwordBytes
    )
    return "${saltBytes.toHex()}:${hashedPassword.toHex()}"
}

fun encodePassword(password : String, salt : String) : String{
    val saltBytes = salt.chunked(2).map{ it.toInt(16).toByte() }
        .toByteArray()

    val digest = MessageDigest.getInstance("SHA-256")
    val passwordBytes = password.toByteArray(StandardCharsets.UTF_8)

    val hashedPassword = digest.digest(
        saltBytes+passwordBytes
    )
    return hashedPassword.toHex()
}

fun ByteArray.toHex() : String{
    var stringToReturn = ""
    for(byte in this.asUByteArray()){
        stringToReturn += byte.toString(16).padStart(2,'0')
    }
    return stringToReturn
}