plugins {
    `java-library`
    `maven-publish`
}

extra["allowedAincraftPrefixes"] = listOf("org/aincraft/math/")
extra["forbiddenAincraftPrefixes"] = listOf(
    "org/aincraft/common/",
    "org/aincraft/config/",
    "org/aincraft/db/",
    "org/aincraft/registry/",
)
extra["paperFree"] = true

apply(from = rootProject.file("gradle/java-conventions.gradle.kts"))
apply(from = rootProject.file("gradle/publish-conventions.gradle.kts"))

dependencies {
    implementation(libs.guava)
}
