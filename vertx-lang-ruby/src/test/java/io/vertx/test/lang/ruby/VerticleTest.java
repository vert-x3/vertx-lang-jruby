package io.vertx.test.lang.ruby;

import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class VerticleTest extends VertxTestBase {

  @Test
  public void testHttpServer() {
    vertx.deployVerticle("examples/simple_http_server.rb", ar -> {
      assertTrue(ar.succeeded());
      HttpClient client = vertx.createHttpClient(new HttpClientOptions());
      client.getNow(8080, "localhost", "/", onSuccess(resp -> {
        assertEquals(200, resp.statusCode());
        testComplete();
      }));
    });
    await();
  }

  @Test
  public void testEventBusPingPong() throws Exception {
    vertx.deployVerticle("examples/bus_echo.rb", ar -> {
      assertTrue(ar.succeeded());
      vertx.eventBus().send("ping-address", "ping", onSuccess(msg -> {
        testComplete();
      }));
    });
    await();
  }
}
