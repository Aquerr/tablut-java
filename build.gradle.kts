plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "io.github.aquerr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.json:json:20230227")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("io.github.aquerr.tablut.Main")
}

tasks.test {
    useJUnitPlatform()
}

javafx {
    modules("javafx.controls")
}