package io.appwish.voteservice.service.impl;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.appwish.grpc.ItemTypeProto;
import io.appwish.grpc.VoteTypeProto;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.Optional;
import java.util.stream.Collectors;

import net.badata.protobuf.converter.Converter;

import io.appwish.grpc.AllVoteQueryProto;
import io.appwish.grpc.AllVoteReplyProto;
import io.appwish.grpc.UpdateVoteInputProto;
import io.appwish.grpc.VoteDeleteReplyProto;
import io.appwish.grpc.VoteInputProto;
import io.appwish.grpc.VoteQueryProto;
import io.appwish.grpc.VoteReplyProto;
import io.appwish.voteservice.TestData;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.ReplyException;
import io.appwish.voteservice.eventbus.Address;
import io.appwish.voteservice.eventbus.Codec;
import io.appwish.voteservice.eventbus.EventBusConfigurer;
import io.appwish.voteservice.model.Vote;
import io.appwish.voteservice.service.GrpcServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class GrpcServiceTest {

  private GrpcServiceImpl grpcService;

  @BeforeEach
  void setUp(final Vertx vertx, final VertxTestContext context) {
    final EventBusConfigurer eventBusConfigurer = new EventBusConfigurer(vertx.eventBus());
    grpcService = new GrpcServiceImpl(vertx.eventBus());
    eventBusConfigurer.registerCodecs();
    context.completeNow();
  }

  @Test
  void should_return_all_votes(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<AllVoteReplyProto> promise = Promise.promise();
    final AllVoteQueryProto query = AllVoteQueryProto.newBuilder().build();
    vertx.eventBus().consumer(Address.FIND_ALL_VOTES.get(), event -> {
      event.reply(TestData.VOTES, new DeliveryOptions().setCodecName(Codec.VOTE.getCodecName()));
    });

    // when
    grpcService.getAllVote(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertEquals(TestData.VOTES, promise.future().result().getVotesList().stream()
          .map(it -> Converter.create().toDomain(Vote.class, it)).collect(Collectors.toList()));
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_when_exception_occured_while_getting_all_votes(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<AllVoteReplyProto> promise = Promise.promise();
    final AllVoteQueryProto query = AllVoteQueryProto.newBuilder().build();
    vertx.eventBus().consumer(Address.FIND_ALL_VOTES.get(), event -> {
      event.fail(0, TestData.ERROR_MESSAGE);
    });

    // when
    grpcService.getAllVote(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().failed());
        assertTrue(promise.future().cause() instanceof ReplyException);
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_one_vote(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<VoteReplyProto> promise = Promise.promise();
    final VoteQueryProto query = VoteQueryProto.newBuilder().setId(TestData.SOME_ID).build();
    vertx.eventBus().consumer(Address.FIND_ONE_VOTE.get(), event -> {
      event.reply(Optional.of(TestData.VOTE_1),
        new DeliveryOptions().setCodecName(Codec.VOTE.getCodecName()));
    });

    // when
    grpcService.getVote(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertEquals(TestData.VOTE_1,
          Converter.create().toDomain(Vote.class, promise.future().result().getVote()));
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_empty_vote_if_not_found(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<VoteReplyProto> promise = Promise.promise();
    final VoteQueryProto query = VoteQueryProto.newBuilder().setId(TestData.SOME_ID).build();
    vertx.eventBus().consumer(Address.FIND_ONE_VOTE.get(), event -> {
      event.reply(Optional.empty(), new DeliveryOptions().setCodecName(Codec.VOTE.getCodecName()));
    });

    // when
    grpcService.getVote(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertNull(promise.future().result());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_while_error_getting_one_vote(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<VoteReplyProto> promise = Promise.promise();
    final VoteQueryProto query = VoteQueryProto.newBuilder().setId(TestData.SOME_ID).build();
    vertx.eventBus().consumer(Address.FIND_ONE_VOTE.get(), event -> {
      event.fail(0, TestData.ERROR_MESSAGE);
    });

    // when
    grpcService.getVote(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().failed());
        assertTrue(promise.future().cause() instanceof ReplyException);
        context.completeNow();
      });
    });
  }

  @Test
  void should_add_and_return_back_vote(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<VoteReplyProto> promise = Promise.promise();
    final VoteInputProto inputProto = VoteInputProto.newBuilder()
      .setUserId(TestData.SOME_USER_ID)
      .setItemId(TestData.SOME_ITEM_ID)
      .setItemType(ItemTypeProto.WISH)
      .setVoteType(VoteTypeProto.UP)
      .build();
    vertx.eventBus().consumer(Address.CREATE_ONE_VOTE.get(), event -> {
      event.reply(TestData.VOTE_1);
    });

    // when
    grpcService.createVote(inputProto, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertEquals(TestData.VOTE_1,
          Converter.create().toDomain(Vote.class, promise.future().result().getVote()));
        context.completeNow();
      });
    });
  }

  @Test
  void should_report_error_while_error_creating_vote(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<VoteReplyProto> promise = Promise.promise();
    final VoteInputProto inputProto = VoteInputProto.newBuilder()
      .setUserId(TestData.SOME_USER_ID)
      .setItemId(TestData.SOME_ITEM_ID)
      .setItemType(ItemTypeProto.WISH)
      .setVoteType(VoteTypeProto.UP)
      .build();
    vertx.eventBus().consumer(Address.CREATE_ONE_VOTE.get(), event -> {
      event.fail(0, TestData.ERROR_MESSAGE);
    });

    // when
    grpcService.createVote(inputProto, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().failed());
        assertTrue(promise.future().cause() instanceof ReplyException);
        context.completeNow();
      });
    });
  }

  @Test
  void should_update_and_return_updated_vote(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<VoteReplyProto> promise = Promise.promise();
    final UpdateVoteInputProto updateVoteInputProto = UpdateVoteInputProto.newBuilder()
      .setUserId(TestData.VOTE_3.getUserId())
      .setItemId(TestData.VOTE_3.getItemId())
      .setItemType(ItemTypeProto.WISH)
      .setVoteType(VoteTypeProto.UP)
      .build();
    vertx.eventBus().consumer(Address.UPDATE_ONE_VOTE.get(), event -> {
      event.reply(Optional.of(TestData.VOTE_3),
        new DeliveryOptions().setCodecName(Codec.VOTE.getCodecName()));
    });

    // when
    grpcService.updateVote(updateVoteInputProto, promise);
    promise.future().setHandler(event -> {

      // then
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertEquals(Converter.create().toDomain(Vote.class, promise.future().result().getVote()),
          TestData.VOTE_3);
        context.completeNow();
      });

    });
  }

  @Test
  void should_not_update_and_return_empty_if_not_found(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<VoteReplyProto> promise = Promise.promise();
    final UpdateVoteInputProto updateVoteInputProto = UpdateVoteInputProto.newBuilder()
      .setUserId(TestData.VOTE_3.getUserId())
      .setItemId(TestData.VOTE_3.getItemId())
      .setItemType(ItemTypeProto.WISH)
      .setVoteType(VoteTypeProto.UP)
      .build();
    vertx.eventBus().consumer(Address.UPDATE_ONE_VOTE.get(), event -> {
      event.reply(Optional.empty(), new DeliveryOptions().setCodecName(Codec.VOTE.getCodecName()));
    });

    // when
    grpcService.updateVote(updateVoteInputProto, promise);
    promise.future().setHandler(event -> {

      // then
      context.verify(() -> {
        assertTrue(event.succeeded());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_while_error_updating_vote(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<VoteReplyProto> promise = Promise.promise();
    final UpdateVoteInputProto updateVoteInputProto = UpdateVoteInputProto.newBuilder()
      .setUserId(TestData.VOTE_3.getUserId())
      .setItemId(TestData.VOTE_3.getItemId())
      .setItemType(ItemTypeProto.WISH)
      .setVoteType(VoteTypeProto.UP)
      .build();
    vertx.eventBus().consumer(Address.UPDATE_ONE_VOTE.get(), event -> {
      event.fail(0, TestData.ERROR_MESSAGE);
    });

    // when
    grpcService.updateVote(updateVoteInputProto, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().failed());
        assertTrue(promise.future().cause() instanceof ReplyException);
        context.completeNow();
      });

    });
  }

  @Test
  void should_delete_vote_and_return_true(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<VoteDeleteReplyProto> promise = Promise.promise();
    final VoteQueryProto query = VoteQueryProto.newBuilder().setId(TestData.SOME_ID).build();
    vertx.eventBus().consumer(Address.DELETE_ONE_VOTE.get(), event -> {
      event.reply(true);
    });

    // when
    grpcService.deleteVote(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertTrue(promise.future().result().getDeleted());
        context.completeNow();
      });
    });
  }

  @Test
  void should_not_delete_and_return_false_if_not_found(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<VoteDeleteReplyProto> promise = Promise.promise();
    final VoteQueryProto query = VoteQueryProto.newBuilder().setId(TestData.SOME_ID).build();
    vertx.eventBus().consumer(Address.DELETE_ONE_VOTE.get(), event -> {
      event.reply(false);
    });

    // when
    grpcService.deleteVote(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertFalse(promise.future().result().getDeleted());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_while_error_deleting_vote(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<VoteDeleteReplyProto> promise = Promise.promise();
    final VoteQueryProto query = VoteQueryProto.newBuilder().setId(TestData.SOME_ID).build();
    vertx.eventBus().consumer(Address.FIND_ALL_VOTES.get(), event -> {
      event.fail(0, TestData.ERROR_MESSAGE);
    });

    // when
    grpcService.deleteVote(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().failed());
        assertTrue(promise.future().cause() instanceof ReplyException);
        context.completeNow();
      });
    });
  }
}
