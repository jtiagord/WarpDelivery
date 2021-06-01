package com.isel.warpDelivery

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import javax.sql.DataSource

@ConstructorBinding
@ConfigurationProperties("app")
data class ConfigProperties (
	val dbConnString: String
)

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
	fun jdbi(dataSource: DataSource): Jdbi = Jdbi.create(dataSource).apply {
		installPlugin(KotlinPlugin())
	}
}

fun main(args: Array<String>) {
	runApplication<WarpDeliveryApplication>(*args)
}
