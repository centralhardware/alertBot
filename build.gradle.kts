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
    implementation("dev.inmo:tgbotapi:27.1.1")
    implementation("com.github.centralhardware:ktgbotapi-commons:beafbfc9a8")
    implementation("io.ktor:ktor-client-core-jvm:3.2.3")
    implementation("dev.inmo:krontab:2.7.2")
    implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.8.0")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}
