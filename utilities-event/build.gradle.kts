plugins {
    `java-library`
    `maven-publish`
}

extra["allowedAincraftPrefixes"] = listOf("org/aincraft/event/")
extra["forbiddenAincraftPrefixes"] = listOf(
    "org/aincraft/common/",
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
    api(libs.annotations)
    api(libs.guava)
}
