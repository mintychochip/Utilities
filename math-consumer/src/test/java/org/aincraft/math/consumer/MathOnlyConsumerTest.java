package org.aincraft.math.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.aincraft.math.RandomSelector;
import org.aincraft.math.RandomSelector.UniformRandomSelector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

class MathOnlyConsumerTest {

  @Test
  void uniformSelectorReturnsSoleAddedElement() {
    UniformRandomSelector<String> selector = RandomSelector.uniform();
    selector.addObject("only");
    assertEquals("only", selector.getObject(new Random(0)));
  }

  @Test
  void mainRunsAgainstShippedMath() {
    MathConsumerMain.main(new String[0]);
  }

  @Test
  void mathArtifactClasspathCannotCompileDbImport(@TempDir Path tmp) throws Exception {
    String mathJar = System.getProperty("math.jar");
    assertNotNull(mathJar, "math.jar system property must point at the math artifact");
    assertTrue(mathJar.endsWith(".jar"), mathJar);

    Path src = tmp.resolve("DbImport.java");
    Files.writeString(src, "import org.aincraft.db.*;\nclass DbImport {}\n");
    Path out = tmp.resolve("classes");
    Files.createDirectory(out);

    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    assertNotNull(compiler, "JDK java compiler required");
    ByteArrayOutputStream errors = new ByteArrayOutputStream();
    int code =
        compiler.run(
            null, null, errors, "-classpath", mathJar, "-d", out.toString(), src.toString());
    String diagnostic = errors.toString(StandardCharsets.UTF_8);
    assertNotEquals(0, code, "db import should fail against math-only classpath: " + diagnostic);
    assertTrue(
        diagnostic.contains("org.aincraft.db"),
        "javac error should mention org.aincraft.db, got: " + diagnostic);
  }
}
