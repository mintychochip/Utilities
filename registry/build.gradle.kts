plugins {
    `java-library`
    `maven-publish`
}

extra["allowedAincraftPrefixes"] = listOf("org/aincraft/registry/")
extra["forbiddenAincraftPrefixes"] = listOf(
    "org/aincraft/common/",
    "org/aincraft/config/",
    "org/aincraft/db/",
    "org/aincraft/math/",
)

apply(from = rootProject.file("gradle/java-conventions.gradle.kts"))
apply(from = rootProject.file("gradle/publish-conventions.gradle.kts"))

dependencies {
    compileOnly(libs.paper.api)
    implementation(libs.guava)
}
