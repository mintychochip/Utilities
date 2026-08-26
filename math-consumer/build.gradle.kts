import org.gradle.api.tasks.bundling.Jar

plugins {
    java
    application
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

application {
    mainClass.set("org.aincraft.math.consumer.MathConsumerMain")
}

dependencies {
    implementation(project(":math"))
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
}

val mathJar = project(":math").tasks.named<Jar>("jar").flatMap { it.archiveFile }

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = false
    }
    dependsOn(project(":math").tasks.named("jar"))
    inputs.file(mathJar)
    doFirst {
        systemProperty("math.jar", mathJar.get().asFile.absolutePath)
    }
}

tasks.named<JavaExec>("run") {
    dependsOn(project(":math").tasks.named("jar"))
}
