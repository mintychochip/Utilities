plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.nmcp)
}

extra["allowedAincraftPrefixes"] = listOf("org/aincraft/minestom/")
extra["forbiddenAincraftPrefixes"] = listOf(
    "org/aincraft/api/",
    "org/aincraft/common/",
    "org/aincraft/bukkit/",
    "org/aincraft/paper/",
    "org/aincraft/config/",
    "org/aincraft/db/",
    "org/aincraft/db/paper/",
    "org/aincraft/math/",
    "org/aincraft/registry/",
    "org/aincraft/event/",
)
extra["bukkitFree"] = true
extra["paperFree"] = true

apply(from = rootProject.file("gradle/java-conventions.gradle.kts"))
apply(from = rootProject.file("gradle/publish-conventions.gradle.kts"))
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

dependencies {
    api(project(":utilities-common"))
    compileOnly(libs.minestom)

    testImplementation(testFixtures(project(":utilities-api")))
    testImplementation(libs.minestom)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
}
