package org.aincraft;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class ConnectionExceptionTest {

  @Test
  void preservesCause() {
    RuntimeException cause = new RuntimeException("boom");
    ConnectionException e = new ConnectionException("failed", cause);
    assertSame(cause, e.getCause());
  }
}
