package io.vertx.test.it;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.collect.EventBusCollector;
import io.vertx.test.core.VertxTestBase;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class VertxUnitTest extends VertxTestBase {

  @org.junit.Test
  public void testAssertionsRuby() throws Exception {
    testAssertions("rb:it/unit/assertions.rb");
  }

  private void testAssertions(String verticle) throws Exception {
    vertx.eventBus().<JsonObject>consumer("assert_tests").bodyStream().handler(msg -> {
      String type = msg.getString("type");
      switch (type) {
        case EventBusCollector.EVENT_TEST_CASE_END:
          String name = msg.getString("name");
          if (name.startsWith("fail_")) {
            assertNotNull(msg.getValue("failure"));
          } else {
            assertEquals(null, msg.getValue("failure"));
          }
          break;
        case EventBusCollector.EVENT_TEST_SUITE_END:
          testComplete();
          break;
      }
    });
    vertx.deployVerticle(verticle, ar -> {
      if (ar.failed()) fail(ar.cause());
      else testComplete();
    });
    await();
  }

  @org.junit.Test
  public void testRubyFailure() {
    vertx.deployVerticle("it/unit/failing.rb", ar -> {
      assertTrue(ar.failed());
      assertEquals("(Exception) the_failure", ar.cause().getMessage());
      testComplete();
    });
    await();
  }
}
