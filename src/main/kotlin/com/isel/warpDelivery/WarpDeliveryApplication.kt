package com.isel.warpDelivery

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.isel.warpDelivery.common.KeyPair
import com.isel.warpDelivery.common.getPrivateKeyFromFile
import com.isel.warpDelivery.common.getPublicKeyFromFile
import com.isel.warpDelivery.routeAPI.RouteApi
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.util.ResourceUtils
import java.io.File
import java.io.FileInputStream
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import javax.sql.DataSource


@ConstructorBinding
@ConfigurationProperties("app")
data class ConfigProperties (
	val dbConnString: String,
	val keysFolder : String
)

private const val PRIVATE_KEY_FILE_NAME = "private_key.pem"
private const val PUBLIC_KEY_FILE_NAME = "public.pem"
@SpringBootApplication
@ConfigurationPropertiesScan
class WarpDeliveryApplication {
	@Bean
	fun dataSource(configProperties: ConfigProperties): DataSource {
		return PGSimpleDataSource().apply {
			setURL(configProperties.dbConnString)
		}
	}

	@Bean
	fun fireStoreDb() : Firestore = FirestoreClient.getFirestore()


	@Bean
	fun JWTKeys(configProperties: ConfigProperties) : KeyPair{
		val privateKey =
			getPrivateKeyFromFile("${configProperties.keysFolder}/$PRIVATE_KEY_FILE_NAME") as RSAPrivateKey
		val publicKey =
			getPublicKeyFromFile("${configProperties.keysFolder}/$PUBLIC_KEY_FILE_NAME") as RSAPublicKey

		return KeyPair(publicKey,privateKey)
	}


	@Bean
	fun jdbi(dataSource: DataSource): Jdbi = Jdbi.create(dataSource).apply {
		installPlugin(KotlinPlugin())
	}
}

fun main(args: Array<String>) {

	/**FIREBASE INITIALIZATION**/
	val file: File = ResourceUtils.getFile("classpath:static/firestoreService.json")
	val serviceAccount = FileInputStream(file)
	val options: FirebaseOptions = FirebaseOptions.builder()
		.setCredentials(GoogleCredentials.fromStream(serviceAccount))
		.build()
	FirebaseApp.initializeApp(options)
	/** END OF FIREBASE INITIALIZATION**/

	runApplication<WarpDeliveryApplication>(*args)
}
