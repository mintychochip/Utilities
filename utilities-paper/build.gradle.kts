plugins {
    `java-library`
    `maven-publish`
}

extra["allowedAincraftPrefixes"] = listOf(
    "org/aincraft/paper/",
)
extra["forbiddenAincraftPrefixes"] = listOf(
    "org/aincraft/api/",
    "org/aincraft/common/",
    "org/aincraft/bukkit/",
    "org/aincraft/config/",
    "org/aincraft/math/",
    "org/aincraft/registry/",
    "org/aincraft/event/",
    "org/aincraft/minestom/",
)

apply(from = rootProject.file("gradle/java-conventions.gradle.kts"))
apply(from = rootProject.file("gradle/publish-conventions.gradle.kts"))

dependencies {
    api(project(":utilities-bukkit"))
    compileOnly(libs.paper.api)
    implementation(libs.guava)

    testImplementation(testFixtures(project(":utilities-api")))
    testImplementation(libs.paper.api)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
}
