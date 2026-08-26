plugins {
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.run.paper) apply false
}

allprojects {
    group = "org.aincraft"
    version = providers.gradleProperty("version").get()
}
