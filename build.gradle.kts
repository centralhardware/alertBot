plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    application
}

group = "me.centralhardware"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

val ktgbotapiVersion = "27.1.2"

dependencies {
    implementation("dev.inmo:tgbotapi:$ktgbotapiVersion")
    implementation("com.github.centralhardware:ktgbotapi-commons:$ktgbotapiVersion-1")
    implementation("io.ktor:ktor-client-core-jvm:3.2.3")
    implementation("dev.inmo:krontab:2.7.2")
    implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.8.0")
    implementation("com.google.api-client:google-api-client:2.2.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.20.0")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20240423-2.0.0")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}
