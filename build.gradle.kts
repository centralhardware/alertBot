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

dependencies {
    implementation("dev.inmo:tgbotapi:27.0.0")
    implementation("com.github.centralhardware:ktgbotapi-commons:0d89074d13")
    implementation("com.clickhouse:clickhouse-jdbc:0.9.0")
    implementation("com.github.seratch:kotliquery:1.9.1")
    implementation("io.ktor:ktor-client-core-jvm:3.2.2")
    implementation("dev.inmo:krontab:2.7.2")
    implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.8.0")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}
