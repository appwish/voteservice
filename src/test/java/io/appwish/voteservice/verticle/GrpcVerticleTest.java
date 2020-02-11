package io.appwish.voteservice.verticle;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.appwish.grpc.AllVoteQueryProto;
import io.appwish.grpc.VoteServiceGrpc;
import io.appwish.voteservice.TestData;
import io.grpc.ManagedChannel;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.grpc.VertxChannelBuilder;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import oi.appwish.voteservice.eventbus.Address;
import oi.appwish.voteservice.eventbus.Codec;
import oi.appwish.voteservice.eventbus.EventBusConfigurer;
import oi.appwish.voteservice.verticle.GrpcVerticle;

@ExtendWith(VertxExtension.class)
class GrpcVerticleTest {

  @Test
  void should_expose_grpc_server(final Vertx vertx, final VertxTestContext context) {
    // given
    final EventBusConfigurer util = new EventBusConfigurer(vertx.eventBus());
    final ManagedChannel channel = VertxChannelBuilder.forAddress(vertx, TestData.APP_HOST, TestData.APP_PORT).usePlaintext(true).build();
    final VoteServiceGrpc.VoteServiceVertxStub serviceStub = new VoteServiceGrpc.VoteServiceVertxStub(channel);
    vertx.deployVerticle(new GrpcVerticle(), new DeploymentOptions(), context.completing());
    vertx.eventBus().consumer(Address.FIND_ALL_VOTES.get(),
      event -> event.reply(TestData.VOTES, new DeliveryOptions().setCodecName(Codec.VOTE.getCodecName())));

    util.registerCodecs();

    // when
    serviceStub.getAllVote(AllVoteQueryProto.newBuilder().build(), event -> {

      // then
      context.verify(() -> {
        assertTrue(event.succeeded());
        context.completeNow();
      });
    });
  }
}
