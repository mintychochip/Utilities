pluginManager.withPlugin("maven-publish") {
    extensions.configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                val component = components.findByName("javaPlatform") ?: components.findByName("java")
                if (component != null) {
                    from(component)
                }
            }
        }
        repositories {
            maven {
                name = "utilitiesGitHubPackages"
                url = uri("https://maven.pkg.github.com/mintychochip/Utilities")
                val gprUser = providers.gradleProperty("gpr.user")
                    .orElse(providers.environmentVariable("GITHUB_ACTOR"))
                val gprKey = providers.gradleProperty("gpr.key")
                    .orElse(providers.environmentVariable("GITHUB_TOKEN"))
                credentials {
                    username = gprUser.getOrElse("")
                    password = gprKey.getOrElse("")
                }
            }
        }
    }
}