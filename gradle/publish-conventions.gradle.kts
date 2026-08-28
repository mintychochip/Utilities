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
