plugins {
    `java-library`
    `maven-publish`
}

extra["allowedAincraftPrefixes"] = listOf(
    "org/aincraft/event/",
    "org/aincraft/math/",
)
extra["forbiddenAincraftPrefixes"] = listOf(
    "org/aincraft/api/",
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

dependencies {
    api(project(":utilities-api"))

    api(libs.annotations)
    api(libs.adventure.api)
    api(libs.adventure.key)
    api(libs.joml)
    api(libs.guava)

    testImplementation(libs.annotations)
    testImplementation(libs.adventure.api)
    testImplementation(libs.adventure.key)
    testImplementation(libs.joml)
    testImplementation(libs.guava)
}
