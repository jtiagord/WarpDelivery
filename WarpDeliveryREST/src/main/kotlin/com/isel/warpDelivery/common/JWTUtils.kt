package com.isel.warpDelivery.common

import com.google.api.client.util.Base64
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*


//BASED ON https://www.baeldung.com/java-read-pem-file-keys


data class KeyPair(val publicKey: RSAPublicKey, val privateKey: RSAPrivateKey)

const val ISSUER = "WARPDELIVERY"

data class JWTPayload(
    val iss : String,
    val userType : String,
    val id : String,
    val exp: Date,
    val iat: Date,
)

fun getPublicKeyFromFile(filename : String): PublicKey{
    val file = File(filename)
    val key = String(Files.readAllBytes(file.toPath()), Charset.defaultCharset())

    val publicKeyPEM = key
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace(System.lineSeparator().toRegex(), "")
        .replace("-----END PUBLIC KEY-----", "")

    val encoded: ByteArray = Base64.decodeBase64(publicKeyPEM)

    val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
    val keySpec = X509EncodedKeySpec(encoded)
    return keyFactory.generatePublic(keySpec)
}

fun getPrivateKeyFromFile(filename : String): PrivateKey{
    val file = File(filename)
    val key = String(Files.readAllBytes(file.toPath()), Charset.defaultCharset())



    val privateKeyPEM = key
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace(System.lineSeparator().toRegex(), "")
        .replace("-----END PRIVATE KEY-----", "")


    val encoded = Base64.decodeBase64(privateKeyPEM)



    val keyFactory = KeyFactory.getInstance("RSA")
    val keySpec = PKCS8EncodedKeySpec(encoded)
    return keyFactory.generatePrivate(keySpec) as RSAPrivateKey
}