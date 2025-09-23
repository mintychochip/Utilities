plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    java
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public")
}

dependencies {
    implementation("org.mongodb:mongodb-driver-sync:5.2.0")
    compileOnly("io.papermc.paper:paper-api:1.21.7-R0.1-SNAPSHOT")
    implementation("com.zaxxer:HikariCP:5.0.1")
}
