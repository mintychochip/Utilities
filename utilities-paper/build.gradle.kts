plugins {
    `java-library`
    `maven-publish`
}

extra["allowedAincraftPrefixes"] = listOf("org/aincraft/paper/")
extra["forbiddenAincraftPrefixes"] = listOf(
    "org/aincraft/config/",
    "org/aincraft/db/",
    "org/aincraft/math/",
    "org/aincraft/registry/",
)

apply(from = rootProject.file("gradle/java-conventions.gradle.kts"))
apply(from = rootProject.file("gradle/publish-conventions.gradle.kts"))

dependencies {
    api(project(":utilities-bukkit"))
    compileOnly(libs.paper.api)

    testImplementation(libs.paper.api)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
}
