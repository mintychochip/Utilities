plugins {
    `java-platform`
    `maven-publish`
}

apply(from = rootProject.file("gradle/publish-conventions.gradle.kts"))

dependencies {
    constraints {
        api(project(":common"))
        api(project(":utilities-bukkit"))
        api(project(":utilities-paper"))
        api(project(":config"))
        api(project(":db-core"))
        api(project(":db-paper"))
        api(project(":math"))
        api(project(":registry"))
    }
}
