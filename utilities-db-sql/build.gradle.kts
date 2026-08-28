plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.nmcp)
}

extra["allowedAincraftPrefixes"] = listOf("org/aincraft/db/sql/")
extra["forbiddenAincraftPrefixes"] = listOf(
    "org/aincraft/api/",
    "org/aincraft/common/",
    "org/aincraft/config/",
    "org/aincraft/registry/",
    "org/aincraft/bukkit/",
    "org/aincraft/paper/",
    "org/aincraft/minestom/",
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
    api(libs.hikari)
    api(libs.jdbi.core)
    implementation(libs.flyway.core)

    testImplementation(libs.sqlite.jdbc)
}
