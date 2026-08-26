plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "utilities"

include("common")
include("utilities")
include("test-plugin")