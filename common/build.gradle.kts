plugins {
    `java-library`
    `maven-publish`
}

extra["allowedAincraftPrefixes"] = listOf("org/aincraft/common/")
extra["forbiddenAincraftPrefixes"] = listOf(
    "org/aincraft/config/",
    "org/aincraft/db/",
    "org/aincraft/math/",
    "org/aincraft/registry/",
)
extra["paperFree"] = true

apply(from = rootProject.file("gradle/java-conventions.gradle.kts"))
apply(from = rootProject.file("gradle/publish-conventions.gradle.kts"))

dependencies {
    api(libs.annotations)
    api(libs.adventure.api)
    api(libs.adventure.key)

    testImplementation(libs.annotations)
    testImplementation(libs.adventure.api)
    testImplementation(libs.adventure.key)
}
