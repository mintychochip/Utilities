import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.run.paper) apply false
    alias(libs.plugins.spotless) apply false
}

allprojects {
    group = "org.aincraft"
    version = providers.gradleProperty("version").get()
}

subprojects {
    val proj = this
    proj.plugins.withId("java") {
        proj.apply(plugin = "com.diffplug.spotless")
        proj.extensions.configure<SpotlessExtension> {
            java {
                googleJavaFormat("1.27.0")
                importOrder("", "java", "javax")
                removeUnusedImports()
                trimTrailingWhitespace()
                endWithNewline()
                target("src/**/*.java")
            }
        }
        proj.tasks.named("check") {
            dependsOn("spotlessCheck")
        }
    }
}
