plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1"
    java
    `java-library`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public")
}

dependencies {
    implementation(project(":utilities")) {
        exclude(group = "io.papermc.paper", module = "paper-api")
    }
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}