plugins {
    `java-library`
    `java-test-fixtures`
    `maven-publish`
    alias(libs.plugins.nmcp)
}

extra["allowedAincraftPrefixes"] = listOf("org/aincraft/api/")
extra["forbiddenAincraftPrefixes"] = listOf(
    "org/aincraft/common/",
    "org/aincraft/config/",
    "org/aincraft/db/",
    "org/aincraft/math/",
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
    api(libs.annotations)
    api(libs.adventure.api)
    api(libs.adventure.key)
    api(libs.joml)
    testFixturesApi(libs.annotations)
    testFixturesApi(libs.adventure.api)
    testFixturesApi(libs.adventure.key)
    testFixturesApi(libs.joml)
    testFixturesApi(platform("org.junit:junit-bom:5.11.3"))
    testFixturesApi("org.junit.jupiter:junit-jupiter")

    testImplementation(libs.annotations)
    testImplementation(libs.adventure.api)
    testImplementation(libs.adventure.key)
    testImplementation(libs.joml)
}
