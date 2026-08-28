import org.gradle.plugins.signing.SigningExtension

pluginManager.withPlugin("maven-publish") {
    pluginManager.apply("signing")

    extensions.configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                val component = components.findByName("javaPlatform") ?: components.findByName("java")
                if (component != null) {
                    from(component)
                }
                pom {
                    name.set("${project.group}:${project.name}")
                    description.set("Cross-platform Minecraft utilities for ${project.name}.")
                    url.set("https://github.com/mintychochip/Utilities")
                    developers {
                        developer {
                            id.set("mintychochip")
                            name.set("mintychochip")
                            url.set("https://github.com/mintychochip")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/mintychochip/Utilities.git")
                        developerConnection.set("scm:git:ssh://git@github.com/mintychochip/Utilities.git")
                        url.set("https://github.com/mintychochip/Utilities")
                    }
                }
            }
        }
    }

    val publishing = extensions.getByType<PublishingExtension>()
    extensions.configure<SigningExtension> {
        val key = providers.environmentVariable("SIGNING_KEY").orNull
        val password = providers.environmentVariable("SIGNING_PASSWORD").orNull
        if (key != null && password != null) {
            useInMemoryPgpKeys(key, password)
            sign(publishing.publications)
        }
    }
}
