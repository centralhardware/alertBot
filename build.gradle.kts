import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.centralhardware"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("dev.inmo:tgbotapi:18.2.1")
    implementation("com.github.centralhardware:telegram-bot-commons:6be88e496b")
    implementation("io.ktor:ktor-client-core-jvm:2.3.12")
    implementation("dev.inmo:krontab:2.5.0")
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
