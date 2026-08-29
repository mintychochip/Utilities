plugins {
    `java-library`
    `java-test-fixtures`
    `maven-publish`
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

dependencies {
    api(libs.annotations)
    api(libs.adventure.api)
    api(libs.adventure.key)
    api(libs.joml)
    testFixturesApi(libs.annotations)
    testFixturesApi(libs.adventure.api)
    testFixturesApi(libs.adventure.key)
    testFixturesApi(libs.joml)
    testFixturesApi(platform("org.junit:junit-bom:6.1.3"))
    testFixturesApi("org.junit.jupiter:junit-jupiter")

    testImplementation(libs.annotations)
    testImplementation(libs.adventure.api)
    testImplementation(libs.adventure.key)
    testImplementation(libs.joml)
}
