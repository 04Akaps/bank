plugins {
    id("org.springframework.boot") version "3.2.3"
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "1.8.0"

    id("io.spring.dependency-management") version "1.0.15.RELEASE"

    kotlin("plugin.serialization") version "1.8.0"
}


group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.3")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.3")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // jwt
    implementation("com.auth0:java-jwt:3.12.0")

    // security
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security:3.2.3")

    // redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.redisson:redisson-spring-boot-starter:3.36.0")

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // client
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    testImplementation(kotlin("test"))
}
