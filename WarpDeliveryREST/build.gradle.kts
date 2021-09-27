import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.6"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.4.32"
	kotlin("plugin.spring") version "1.4.32"
}


group = "com.isel"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {

	implementation(platform("com.google.cloud:libraries-bom:20.8.0"))

	compile("com.google.cloud:google-cloud-pubsub")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.google.firebase:firebase-admin:7.3.0")
	implementation("org.jdbi:jdbi3-core:3.18.1")
	implementation("org.jdbi:jdbi3-kotlin:3.18.1")
	implementation("org.postgresql:postgresql:42.2.19")
	implementation("com.auth0:java-jwt:3.18.1")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}



tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
