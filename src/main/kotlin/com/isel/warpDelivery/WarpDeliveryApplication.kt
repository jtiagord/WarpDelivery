package com.isel.warpDelivery

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.isel.warpDelivery.authentication.AccessControlInterceptor
import com.isel.warpDelivery.common.KeyPair
import com.isel.warpDelivery.common.getPrivateKeyFromFile
import com.isel.warpDelivery.common.getPublicKeyFromFile
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
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


@Configuration
@EnableWebMvc
class ApiConfig : WebMvcConfigurer {
	override fun addInterceptors(registry: InterceptorRegistry) {
		registry.addInterceptor(AccessControlInterceptor())
	}
}

fun main(args: Array<String>) {
	val options: FirebaseOptions = FirebaseOptions.builder()
		.setCredentials(GoogleCredentials.getApplicationDefault())
		.build()
	FirebaseApp.initializeApp(options)
	/** END OF FIREBASE INITIALIZATION**/

	runApplication<WarpDeliveryApplication>(*args)
}
