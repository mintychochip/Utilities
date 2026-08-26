plugins {
    id("signing")
    id("com.vanniktech.maven.publish") version "0.34.0"
    id("com.gradleup.nmcp") version "1.0.0"
    java
    `java-library`
    `maven-publish`
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly("org.jetbrains:annotations:26.0.2")
    compileOnly("net.kyori:adventure-api:4.18.0")
    compileOnly("net.kyori:adventure-key:4.18.0")

    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.jetbrains:annotations:26.0.2")
    testImplementation("net.kyori:adventure-api:4.18.0")
    testImplementation("net.kyori:adventure-key:4.18.0")
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    pom {
        name.set("common")
        description.set("Domain agnostic interface types and contracts for minecraft")
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
        useInMemoryPgpKeys(key, password)
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
