package io.appwish.voteservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.appwish.grpc.HasVotedReplyProto;
import io.appwish.grpc.ItemTypeProto;
import io.appwish.grpc.UnvoteReplyProto;
import io.appwish.grpc.VoteInputProto;
import io.appwish.grpc.VoteReplyProto;
import io.appwish.grpc.VoteScoreReplyProto;
import io.appwish.grpc.VoteSelectorProto;
import io.appwish.grpc.VoteTypeProto;
import io.appwish.voteservice.TestData;
import io.appwish.voteservice.dto.Score;
import io.appwish.voteservice.dto.Vote;
import io.appwish.voteservice.dto.query.VoteSelector;
import io.appwish.voteservice.dto.type.ItemType;
import io.appwish.voteservice.eventbus.Address;
import io.appwish.voteservice.eventbus.Codec;
import io.appwish.voteservice.eventbus.EventBusConfigurer;
import io.appwish.voteservice.service.GrpcServiceImpl;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import net.badata.protobuf.converter.Converter;
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
  void should_add_and_return_back_vote(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<VoteReplyProto> promise = Promise.promise();
    final VoteInputProto inputProto = VoteInputProto.newBuilder()
        .setItemId(TestData.SOME_ITEM_ID)
        .setItemType(ItemTypeProto.WISH)
        .setVoteType(VoteTypeProto.UP)
        .build();

    vertx.eventBus().consumer(Address.VOTE.get(), event -> {
      event.reply(TestData.VOTE_1);
    });

    // when
    grpcService.vote(inputProto, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertEquals(TestData.VOTE_1, Converter.create().toDomain(Vote.class, promise.future().result().getVote()));
        context.completeNow();
      });
    });
  }

  @Test
  void should_check_if_user_has_already_voted(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<HasVotedReplyProto> promise = Promise.promise();
    final VoteSelectorProto selectorProto = VoteSelectorProto.newBuilder()
        .setItemId(TestData.SOME_ITEM_ID)
        .setItemType(ItemTypeProto.WISH)
        .build();

    vertx.eventBus().<VoteSelector>consumer(Address.HAS_VOTED.get(), event -> {
      if (event.body().getItemId() != TestData.SOME_ITEM_ID || event.body().getItemType() != ItemType.WISH) {
        // TODO mock UserContextInterceptor
        // event.headers().contains("userId")
        event.fail(1, "Passed incorrect payload");
        return;
      }
      event.reply(true);
    });

    // when
    grpcService.hasVoted(selectorProto, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertTrue(promise.future().result().getVoted());
        context.completeNow();
      });
    });
  }

  @Test
  void should_check_item_vote_score(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<VoteScoreReplyProto> promise = Promise.promise();
    final VoteSelectorProto selectorProto = VoteSelectorProto.newBuilder()
        .setItemId(TestData.SOME_ITEM_ID)
        .setItemType(ItemTypeProto.WISH)
        .build();

    vertx.eventBus().<VoteSelector>consumer(Address.VOTE_SCORE.get(), event -> {
      if (event.body().getItemId() != TestData.SOME_ITEM_ID || event.body().getItemType() != ItemType.WISH) {
        event.fail(1, "Passed incorrect payload");
        return;
      }
      event.reply(new Score(20, 5));
    });

    // when
    grpcService.voteScore(selectorProto, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertEquals(20, event.result().getUp());
        assertEquals(5, event.result().getDown());
        assertEquals(15, event.result().getScore());
        context.completeNow();
      });
    });
  }

  @Test
  void should_report_error_while_error_adding_vote(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<VoteReplyProto> promise = Promise.promise();
    final VoteInputProto inputProto = VoteInputProto.newBuilder()
        .setItemId(TestData.SOME_ITEM_ID)
        .setItemType(ItemTypeProto.WISH)
        .setVoteType(VoteTypeProto.UP)
        .build();

    vertx.eventBus().consumer(Address.VOTE.get(), event -> {
      event.fail(0, TestData.ERROR_MESSAGE);
    });

    // when
    grpcService.vote(inputProto, promise);

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
    final VoteInputProto updateVoteInputProto = VoteInputProto.newBuilder()
        .setItemId(TestData.VOTE_3.getItemId())
        .setItemType(ItemTypeProto.WISH)
        .setVoteType(VoteTypeProto.UP)
        .build();

    vertx.eventBus().consumer(Address.VOTE.get(), event -> {
      event.reply(TestData.VOTE_3,
          new DeliveryOptions().setCodecName(Codec.VOTE.getCodecName()));
    });

    // when
    grpcService.vote(updateVoteInputProto, promise);
    promise.future().setHandler(event -> {

      // then
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertEquals(Converter.create().toDomain(Vote.class, promise.future().result().getVote()), TestData.VOTE_3);
        context.completeNow();
      });
    });
  }

  @Test
  void should_not_update_and_return_empty_if_not_found(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<VoteReplyProto> promise = Promise.promise();
    final VoteInputProto updateVoteInputProto = VoteInputProto.newBuilder()
        .setItemId(TestData.VOTE_3.getItemId())
        .setItemType(ItemTypeProto.WISH)
        .setVoteType(VoteTypeProto.UP)
        .build();

    vertx.eventBus().consumer(Address.VOTE.get(), event -> {
      event.reply(null, new DeliveryOptions().setCodecName(Codec.VOTE.getCodecName()));
    });

    // when
    grpcService.vote(updateVoteInputProto, promise);
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
    final VoteInputProto updateVoteInputProto = VoteInputProto.newBuilder()
        .setItemId(TestData.VOTE_3.getItemId())
        .setItemType(ItemTypeProto.WISH)
        .setVoteType(VoteTypeProto.UP)
        .build();

    vertx.eventBus().consumer(Address.VOTE.get(), event -> {
      event.fail(0, TestData.ERROR_MESSAGE);
    });

    // when
    grpcService.vote(updateVoteInputProto, promise);

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
    final Promise<UnvoteReplyProto> promise = Promise.promise();
    final VoteSelectorProto query = VoteSelectorProto.newBuilder()
        .setItemId(TestData.SOME_ITEM_ID)
        .setItemType(ItemTypeProto.COMMENT)
        .build();

    vertx.eventBus().consumer(Address.UNVOTE.get(), event -> {
      event.reply(true);
    });

    // when
    grpcService.unvote(query, promise);

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
    final Promise<UnvoteReplyProto> promise = Promise.promise();
    final VoteSelectorProto query = VoteSelectorProto.newBuilder()
        .setItemId(TestData.SOME_ITEM_ID)
        .setItemType(ItemTypeProto.COMMENT)
        .build();

    vertx.eventBus().consumer(Address.UNVOTE.get(), event -> {
      event.reply(false);
    });

    // when
    grpcService.unvote(query, promise);

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
    final Promise<UnvoteReplyProto> promise = Promise.promise();
    final VoteSelectorProto query = VoteSelectorProto.newBuilder()
        .setItemId(TestData.SOME_ITEM_ID)
        .setItemType(ItemTypeProto.COMMENT)
        .build();

    vertx.eventBus().consumer(Address.UNVOTE.get(), event -> {
      event.fail(1, TestData.ERROR_MESSAGE);
    });

    // when
    grpcService.unvote(query, promise);

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
