package org.aincraft;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.ResultSet;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
final class ResourceExtractor {

  private ResourceExtractor() {

  }

  static InputStream getResourceStream(String filePath) throws FileNotFoundException {
    ClassLoader loader = ResourceExtractor.class.getClassLoader();
    InputStream resourceStream = loader.getResourceAsStream(filePath);
    if (resourceStream == null) {
      throw new FileNotFoundException(filePath);
    }
    return resourceStream;
  }
}
