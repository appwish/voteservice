package io.appwish.voteservice.verticle;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.appwish.grpc.VoteSelectorProto;
import io.appwish.grpc.VoteServiceGrpc;
import io.appwish.voteservice.TestData;
import io.appwish.voteservice.eventbus.Address;
import io.appwish.voteservice.eventbus.EventBusConfigurer;
import io.grpc.ManagedChannel;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.grpc.VertxChannelBuilder;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class GrpcVerticleTest {

  @Test
  void should_expose_grpc_server(final Vertx vertx, final VertxTestContext context) {
    // given
    final EventBusConfigurer util = new EventBusConfigurer(vertx.eventBus());
    final ManagedChannel channel = VertxChannelBuilder.forAddress(vertx, TestData.APP_HOST, TestData.APP_PORT).usePlaintext(true).build();
    final VoteServiceGrpc.VoteServiceVertxStub serviceStub = new VoteServiceGrpc.VoteServiceVertxStub(channel);
    vertx.deployVerticle(new GrpcVerticle(), new DeploymentOptions(), context.completing());
    vertx.eventBus().consumer(Address.HAS_VOTED.get(), event -> event.reply(false));

    util.registerCodecs();

    // when
    serviceStub.hasVoted(VoteSelectorProto.newBuilder().build(), event -> {

      // then
      context.verify(() -> {
        assertTrue(event.succeeded());
        context.completeNow();
      });
    });
  }
}
