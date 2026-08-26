plugins {
    `java-library`
    `maven-publish`
}

extra["allowedAincraftPrefixes"] = listOf("org/aincraft/db/")
extra["forbiddenAincraftPrefixes"] = listOf(
    "org/aincraft/db/paper/",
    "org/aincraft/common/",
    "org/aincraft/config/",
    "org/aincraft/math/",
    "org/aincraft/registry/",
)
extra["paperFree"] = true

apply(from = rootProject.file("gradle/java-conventions.gradle.kts"))
apply(from = rootProject.file("gradle/publish-conventions.gradle.kts"))

dependencies {
    api(libs.hikari)
    api(libs.mongodb.sync)
    testImplementation(libs.sqlite.jdbc)
}
