plugins {
    id("org.springframework.boot") version "3.0.0"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    kotlin("plugin.spring") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    kotlin("jvm") version "2.0.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.slf4j:slf4j-api:1.7.32")

    // jwt
    implementation("com.auth0:java-jwt:3.12.0")

    testImplementation(kotlin("test"))
}
