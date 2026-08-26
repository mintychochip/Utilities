plugins {
    `java-library`
    `maven-publish`
}

extra["allowedAincraftPrefixes"] = listOf("org/aincraft/bukkit/")
extra["forbiddenAincraftPrefixes"] = listOf(
    "org/aincraft/paper/",
    "org/aincraft/config/",
    "org/aincraft/db/",
    "org/aincraft/math/",
    "org/aincraft/registry/",
)
extra["paperFree"] = true

apply(from = rootProject.file("gradle/java-conventions.gradle.kts"))
apply(from = rootProject.file("gradle/publish-conventions.gradle.kts"))

dependencies {
    api(project(":common"))
    api(libs.adventure.text.serializer.legacy)
    compileOnly(libs.spigot.api)

    testImplementation(libs.spigot.api)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
}
