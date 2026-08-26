plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    java
    `java-library`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencies {
    implementation(project(":utilities")) {
        exclude(group = "io.papermc.paper", module = "paper-api")
    }
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    testImplementation(project(":utilities"))
    testCompileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showStandardStreams = false
        }
    }

    shadowJar {
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion("1.21.4")
    }
}
