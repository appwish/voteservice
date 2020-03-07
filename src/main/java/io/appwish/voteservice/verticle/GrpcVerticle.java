package io.appwish.voteservice.verticle;


import io.grpc.BindableService;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;
import io.appwish.voteservice.service.GrpcServiceImpl;

/**
 * Verticle responsible for spinning up the gRPC server.
 */
public class GrpcVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcVerticle.class.getName());
  private static final String APP_PORT = "appPort";
  private static final String APP_HOST = "appHost";

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    final ConfigRetriever retriever = ConfigRetriever.create(vertx);
    final BindableService grpcVoteService = new GrpcServiceImpl(vertx.eventBus());

    retriever.getConfig(event -> {
      final JsonObject config = event.result();
      final String appHost = config.getString(APP_HOST);
      final Integer appPort = config.getInteger(APP_PORT);

      final VertxServer server = VertxServerBuilder
        .forAddress(vertx, appHost, appPort)
        .addService(grpcVoteService)
        .build();

      server.start(asyncResult -> {
        if (asyncResult.succeeded()) {
          LOG.info("GrpcServiceImpl gRPC server started on port: " + appPort);
          startPromise.complete();
        } else {
          LOG.error(
            "Could not start GrpcServiceImpl gRPC server: " + asyncResult.cause().getMessage());
          startPromise.fail(asyncResult.cause());
        }
      });
    });
  }
}
