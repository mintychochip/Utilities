package org.aincraft;

import java.io.FileNotFoundException;
import java.io.InputStream;

public final class Utilities {

  private Utilities() {
    throw new UnsupportedOperationException("failed to create");
  }

  public static InputStream getResourceStream(String filePath) throws FileNotFoundException {
    ClassLoader loader = Utilities.class.getClassLoader();
    java.io.InputStream resourceStream = loader.getResourceAsStream(filePath);
    if (resourceStream == null) {
      throw new FileNotFoundException(filePath);
    }
    return resourceStream;
  }
}
