import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.centralhardware"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

val clickhouseVersion = "0.7.2"

dependencies {
    implementation("dev.inmo:tgbotapi:23.0.0")
    implementation("com.github.centralhardware:telegram-bot-commons:34c054a4a1")
    implementation("com.clickhouse:clickhouse-jdbc:$clickhouseVersion")
    implementation("com.clickhouse:clickhouse-http-client:$clickhouseVersion")
    implementation("com.github.seratch:kotliquery:1.9.0")
    implementation("io.ktor:ktor-client-core-jvm:3.0.3")
    implementation("dev.inmo:krontab:2.7.0")
    implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.7.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("shadow")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "MainKt"))
        }
    }
}