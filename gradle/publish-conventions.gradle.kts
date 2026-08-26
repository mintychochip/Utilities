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
    }
}
