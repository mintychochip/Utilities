plugins {
    `java-platform`
    `maven-publish`
}

apply(from = rootProject.file("gradle/publish-conventions.gradle.kts"))

dependencies {
    constraints {
        api(project(":utilities-api"))
        api(project(":utilities-common"))
        api(project(":utilities-db-sql"))
        api(project(":utilities-bukkit"))
        api(project(":utilities-paper"))
        api(project(":utilities-minestom"))
    }
}
