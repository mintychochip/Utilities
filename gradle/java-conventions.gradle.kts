import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.jvm.tasks.Jar
import java.util.jar.JarFile

extensions.configure<JavaPluginExtension> {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    withSourcesJar()
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = false
    }
}

dependencies {
    add("testImplementation", platform("org.junit:junit-bom:5.11.3"))
    add("testImplementation", "org.junit.jupiter:junit-jupiter")
}

tasks.register("printCompileClasspath") {
    val compileCp = configurations.named("compileClasspath")
    inputs.files(compileCp)
    doLast {
        println(compileCp.get().asPath)
    }
}

tasks.register("printRuntimeClasspath") {
    val runtimeCp = configurations.named("runtimeClasspath")
    inputs.files(runtimeCp)
    doLast {
        println(runtimeCp.get().asPath)
    }
}

@Suppress("UNCHECKED_CAST")
val allowedPrefixes: List<String> =
    if (extra.has("allowedAincraftPrefixes")) {
        extra["allowedAincraftPrefixes"] as List<String>
    } else {
        emptyList()
    }

@Suppress("UNCHECKED_CAST")
val forbiddenPrefixes: List<String> =
    if (extra.has("forbiddenAincraftPrefixes")) {
        extra["forbiddenAincraftPrefixes"] as List<String>
    } else {
        emptyList()
    }

if (allowedPrefixes.isNotEmpty()) {
    val isolation = tasks.register("verifyJarIsolation") {
        val jarFile = tasks.named<Jar>("jar").flatMap { it.archiveFile }
        inputs.file(jarFile)
        inputs.property("allowed", allowedPrefixes)
        inputs.property("forbidden", forbiddenPrefixes)
        doLast {
            JarFile(jarFile.get().asFile).use { jar ->
                val classEntries = jar.entries().asSequence()
                    .map { it.name }
                    .filter { it.endsWith(".class") && it.startsWith("org/aincraft/") }
                    .toList()
                check(classEntries.isNotEmpty()) {
                    "${jarFile.get().asFile.name} contains no org.aincraft classes"
                }
                val matchingAllowed = classEntries.filter { name ->
                    allowedPrefixes.any { name.startsWith(it) }
                }
                check(matchingAllowed.isNotEmpty()) {
                    "${jarFile.get().asFile.name} is missing classes under $allowedPrefixes"
                }
                for (name in classEntries) {
                    val forbidden = forbiddenPrefixes.firstOrNull { name.startsWith(it) }
                    check(forbidden == null) {
                        "${jarFile.get().asFile.name} contains foreign class $name (prefix $forbidden)"
                    }
                    val allowed = allowedPrefixes.any { name.startsWith(it) }
                    check(allowed) {
                        "${jarFile.get().asFile.name} contains foreign class $name (not under $allowedPrefixes)"
                    }
                }
            }
        }
    }
    tasks.named("check") { dependsOn(isolation) }
}

val bukkitFree = extra.has("bukkitFree") && extra["bukkitFree"] == true
val paperFree = bukkitFree || (extra.has("paperFree") && extra["paperFree"] == true)

if (paperFree) {
    val noPaper = tasks.register("verifyNoPaperOnCompileClasspath") {
        val compileCp = configurations.named("compileClasspath")
        inputs.files(compileCp)
        doLast {
            val names = compileCp.get().files.map { it.name }
            check(names.none { it.contains("paper-api") }) {
                "paper-api leaked onto compile classpath: $names"
            }
        }
    }
    val noPaperImports = tasks.register("verifyNoPaperImports") {
        val sources = fileTree("src/main/java") {
            include("**/*.java")
        }
        inputs.files(sources)
        doLast {
            val hits = sources.files.filter { it.readText().contains("import io.papermc") }
            check(hits.isEmpty()) {
                "io.papermc imports in paper-free module: ${hits.map { it.relativeTo(projectDir) }}"
            }
        }
    }
    tasks.named("check") {
        dependsOn(noPaper, noPaperImports)
    }
}

if (bukkitFree) {
    val noBukkitImports = tasks.register("verifyNoBukkitImports") {
        val sources = fileTree("src/main/java") {
            include("**/*.java")
        }
        inputs.files(sources)
        doLast {
            val hits = sources.files.filter { it.readText().contains("import org.bukkit") }
            check(hits.isEmpty()) {
                "org.bukkit imports in bukkit-free module: ${hits.map { it.relativeTo(projectDir) }}"
            }
        }
    }
    tasks.named("check") {
        dependsOn(noBukkitImports)
    }
}
