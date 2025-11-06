plugins {
    id("signing")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.vanniktech.maven.publish") version "0.34.0"
    id("com.gradleup.nmcp") version "1.0.0"
    java
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public")
}

dependencies {
    implementation("org.mongodb:mongodb-driver-sync:5.2.0")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    implementation("com.zaxxer:HikariCP:5.0.1")
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications() // still fine to keep; won’t hurt

    pom {
        name.set("utilities")
        description.set("a utilities library for minecraft")
        url.set("https://github.com/mintychochip/PacketBlocks")
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        scm {
            url.set("https://github.com/mintychochip/PacketBlocks")
            connection.set("scm:git:https://github.com/mintychochip/PacketBlocks.git")
            developerConnection.set("scm:git:ssh://[email protected]:mintychochip/PacketBlocks.git")
        }
        developers {
            developer {
                id.set("mintychochip")
                name.set("mintychochip")
                email.set("[email protected]")
            }
        }
    }
}

signing {
    val key = providers.environmentVariable("SIGNING_KEY").orNull
    var password = providers.environmentVariable("SIGNING_PASSWORD").orNull
    if (key != null && password != null) {
        useInMemoryPgpKeys(key,password)
        sign(publishing.publications)
    } else {
        logger.warn("Signing disabled: SIGNING_KEY OR SIGNING_PASSWORD missing")
    }
}

nmcp {
    publishAllPublicationsToCentralPortal {
        username.set(
            providers.gradleProperty("mavenCentralUsername")
                .orElse(providers.environmentVariable("MAVEN_USERNAME"))
        )
        password.set(
            providers.gradleProperty("mavenCentralPassword")
                .orElse(providers.environmentVariable("MAVEN_PASSWORD"))
        )
        publishingType.set("AUTOMATIC")
    }
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
}