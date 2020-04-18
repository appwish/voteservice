package io.appwish.voteservice.verticle;

import io.appwish.voteservice.interceptor.ExceptionDetailsInterceptor;
import io.appwish.voteservice.interceptor.UserContextInterceptor;
import io.appwish.voteservice.service.GrpcServiceImpl;
import io.grpc.BindableService;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.grpc.VertxServer;
import io.vertx.grpc.VertxServerBuilder;

/**
 * Verticle responsible for spinning up the gRPC server.
 */
public class GrpcVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcVerticle.class.getName());
  private static final String APP_PORT = "appPort";
  private static final String APP_HOST = "appHost";

  @Override
  public void start(final Promise<Void> startPromise) {
    final BindableService grpcVoteService = new GrpcServiceImpl(vertx.eventBus());
    final ConfigStoreOptions envs = new ConfigStoreOptions().setType("env");
    final ConfigStoreOptions fileStore = new ConfigStoreOptions().setType("file").setConfig(new JsonObject().put("path", "conf/config.json"));
    final ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(fileStore).addStore(envs);
    final ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

    retriever.getConfig(event -> {
      final JsonObject config = event.result();
      final String appHost = config.getString(APP_HOST);
      final Integer appPort = config.getInteger(APP_PORT);

      final VertxServer server = VertxServerBuilder
          .forAddress(vertx, appHost, appPort)
          .intercept(new UserContextInterceptor())
          .addService(grpcVoteService)
          .intercept(new ExceptionDetailsInterceptor())
          .build();

      server.start(asyncResult -> {
        if (asyncResult.succeeded()) {
          LOG.info("GrpcServiceImpl gRPC server started on port: " + appPort);
          startPromise.complete();
        } else {
          LOG.error("Could not start GrpcServiceImpl gRPC server: " + asyncResult.cause().getMessage());
          startPromise.fail(asyncResult.cause());
        }
      });
    });
  }
}
