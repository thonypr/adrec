import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.0.0"
    application
    id("io.github.goooler.shadow") version "8.1.8"
}

group = "com.adrec"
version = "1.0.0"

application {
    mainClass.set("com.adrec.ApplicationKt")
}

repositories {
    mavenCentral()
}

val ktorVersion = "2.3.10"
val exposedVersion = "0.50.1"

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // DB
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.flywaydb:flyway-core:10.11.0")
    implementation("org.flywaydb:flyway-database-postgresql:10.11.0")

    // Telegram bot
    implementation("org.telegram:telegrambots:6.9.7.1")

    // Google API client (YouTube)
    implementation("com.google.apis:google-api-services-youtube:v3-rev20240514-2.0.0")
    implementation("com.google.http-client:google-http-client-gson:1.44.1")
    implementation("com.google.oauth-client:google-oauth-client:1.36.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.23.0")

    // Config & logging
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    mergeServiceFiles()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
