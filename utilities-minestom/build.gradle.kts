plugins {
    `java-library`
    `maven-publish`
}

extra["allowedAincraftPrefixes"] = listOf("org/aincraft/minestom/")
extra["forbiddenAincraftPrefixes"] = listOf(
    "org/aincraft/bukkit/",
    "org/aincraft/paper/",
    "org/aincraft/config/",
    "org/aincraft/db/",
    "org/aincraft/math/",
    "org/aincraft/registry/",
)
extra["bukkitFree"] = true
extra["paperFree"] = true

apply(from = rootProject.file("gradle/java-conventions.gradle.kts"))
apply(from = rootProject.file("gradle/publish-conventions.gradle.kts"))

dependencies {
    api(project(":common"))
    compileOnly(libs.minestom)

    testImplementation(libs.minestom)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
}
