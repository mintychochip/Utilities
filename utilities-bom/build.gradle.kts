plugins {
    `java-platform`
    `maven-publish`
    alias(libs.plugins.nmcp)
}

apply(from = rootProject.file("gradle/publish-conventions.gradle.kts"))
nmcp {
    publishAllPublicationsToCentralPortal {
        username.set(
            providers.gradleProperty("mavenCentralUsername")
                .orElse(providers.environmentVariable("MAVEN_USERNAME"))
        )
        password.set(
            providers.gradleProperty("mavenCentralPassword")
                .orElse(providers.environmentVariable("MAVEN_PASSWORD"))
        )
        publishingType.set("AUTOMATIC")
    }
}

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
