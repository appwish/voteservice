package io.appwish.voteservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.appwish.voteservice.TestData;
import io.appwish.voteservice.eventbus.Address;
import io.appwish.voteservice.eventbus.EventBusConfigurer;
import io.appwish.voteservice.model.Vote;
import io.appwish.voteservice.model.query.VoteSelector;
import io.appwish.voteservice.repository.VoteRepository;
import io.appwish.voteservice.service.DatabaseService;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class DatabaseServiceTest {

  private static final String USER_ID = "userId";

  private DatabaseService databaseService;
  private VoteRepository voteRepository;

  @BeforeEach
  void setUp(final Vertx vertx, final VertxTestContext context) {
    final EventBusConfigurer util = new EventBusConfigurer(vertx.eventBus());
    voteRepository = mock(VoteRepository.class);
    databaseService = new DatabaseService(vertx.eventBus(), voteRepository);
    databaseService.registerEventBusEventHandlers();
    util.registerCodecs();
    context.completeNow();
  }

  @Test
  void should_reply_added_vote_if_not_voted_before(final Vertx vertx, final VertxTestContext context) {
    // given
    when(voteRepository.vote(TestData.VOTE_INPUT_1, TestData.SOME_USER_ID)).thenReturn(Future.succeededFuture(TestData.VOTE_1));
    when(voteRepository.hasVoted(TestData.VOTE_SELECTOR, TestData.SOME_USER_ID)).thenReturn(Future.succeededFuture(false));
    final DeliveryOptions options = new DeliveryOptions().addHeader(USER_ID, TestData.SOME_USER_ID);

    // when
    vertx.eventBus().<Vote>request(Address.VOTE.get(), TestData.VOTE_INPUT_1, options, event -> {

      // then
      context.verify(() -> {
        assertEquals(TestData.VOTE_1, event.result().body());
        context.completeNow();
      });
    });
  }

  @Test
  void should_update_vote_if_voted_before(final Vertx vertx, final VertxTestContext context) {
    // given
    when(voteRepository.updateVote(TestData.VOTE_INPUT_1, TestData.SOME_USER_ID)).thenReturn(Future.succeededFuture(TestData.VOTE_1));
    when(voteRepository.hasVoted(TestData.VOTE_SELECTOR, TestData.SOME_USER_ID)).thenReturn(Future.succeededFuture(true));
    final DeliveryOptions options = new DeliveryOptions().addHeader(USER_ID, TestData.SOME_USER_ID);

    // when
    vertx.eventBus().<Vote>request(Address.VOTE.get(), TestData.VOTE_INPUT_1, options, event -> {

      // then
      context.verify(() -> {
        assertEquals(TestData.VOTE_1, event.result().body());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_on_error_adding_vote(final Vertx vertx, final VertxTestContext context) {
    // given
    when(voteRepository.vote(TestData.VOTE_INPUT_1, TestData.SOME_USER_ID)).thenReturn(Future.failedFuture(new AssertionError()));
    when(voteRepository.hasVoted(TestData.VOTE_SELECTOR, TestData.SOME_USER_ID)).thenReturn(Future.succeededFuture(false));
    final DeliveryOptions options = new DeliveryOptions().addHeader(USER_ID, TestData.SOME_USER_ID);

    // when
    vertx.eventBus().<Vote>request(Address.VOTE.get(), TestData.VOTE_INPUT_1, options, event -> {

      // then
      context.verify(() -> {
        assertTrue(event.failed());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_true_if_deleted_vote(final Vertx vertx, final VertxTestContext context) {
    // given
    final DeliveryOptions options = new DeliveryOptions().addHeader(USER_ID, TestData.SOME_USER_ID);
    when(voteRepository.unvote(TestData.VOTE_SELECTOR, TestData.SOME_USER_ID)).thenReturn(Future.succeededFuture(true));
    when(voteRepository.hasVoted(TestData.VOTE_SELECTOR, TestData.SOME_USER_ID)).thenReturn(Future.succeededFuture(true));

    // when
    vertx.eventBus().<Boolean>request(Address.UNVOTE.get(), TestData.VOTE_SELECTOR, options, event -> {

      // then
      context.verify(() -> {
        assertTrue(event.succeeded());
        assertEquals(true, event.result().body());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_false_if_not_deleted_vote(final Vertx vertx, final VertxTestContext context) {
    // given
    final DeliveryOptions options = new DeliveryOptions().addHeader(USER_ID, TestData.SOME_USER_ID);
    when(voteRepository.unvote(TestData.VOTE_SELECTOR, TestData.SOME_USER_ID)).thenReturn(Future.succeededFuture(false));
    when(voteRepository.hasVoted(TestData.VOTE_SELECTOR, TestData.SOME_USER_ID)).thenReturn(Future.succeededFuture(true));

    // when
    vertx.eventBus().<Boolean>request(Address.UNVOTE.get(),
        TestData.VOTE_SELECTOR, options, event -> {

          // then
          context.verify(() -> {
            assertTrue(event.succeeded());
            assertEquals(false, event.result().body());
            context.completeNow();
          });
        });
  }

  @Test
  void should_not_delete_vote_if_not_voted_before(final Vertx vertx, final VertxTestContext context) {
    // given
    final DeliveryOptions options = new DeliveryOptions().addHeader(USER_ID, TestData.SOME_USER_ID);
    when(voteRepository.hasVoted(TestData.VOTE_SELECTOR, TestData.SOME_USER_ID)).thenReturn(Future.succeededFuture(true));
    when(voteRepository.unvote(TestData.VOTE_SELECTOR, TestData.SOME_USER_ID)).thenReturn(Future.succeededFuture(false));

    // when
    vertx.eventBus().<Boolean>request(Address.UNVOTE.get(),
        TestData.VOTE_SELECTOR, options, event -> {

          // then
          context.verify(() -> {
            assertTrue(event.succeeded());
            assertFalse(event.result().body());
            context.completeNow();
          });
        });
  }

  @Test
  void should_return_error_on_error_deleting_vote(final Vertx vertx, final VertxTestContext context) {
    // given
    final DeliveryOptions options = new DeliveryOptions().addHeader(USER_ID, TestData.SOME_USER_ID);
    when(voteRepository.unvote(TestData.VOTE_SELECTOR, TestData.SOME_USER_ID)).thenReturn(Future.failedFuture(new AssertionError()));
    when(voteRepository.hasVoted(TestData.VOTE_SELECTOR, TestData.SOME_USER_ID)).thenReturn(Future.succeededFuture(true));

    // when
    vertx.eventBus().<Boolean>request(Address.UNVOTE.get(),
        TestData.VOTE_SELECTOR, options, event -> {

          // then
          context.verify(() -> {
            assertTrue(event.failed());
            context.completeNow();
          });
        });
  }

  @Test
  void should_return_empty_if_vote_not_updated(final Vertx vertx, final VertxTestContext context) {
    // given
    final DeliveryOptions options = new DeliveryOptions().addHeader(USER_ID, "4040");
    when(voteRepository.hasVoted(new VoteSelector(TestData.UPDATE_VOTE_INPUT.getItemId(), TestData.UPDATE_VOTE_INPUT.getItemType()), "4040"))
        .thenReturn(Future.succeededFuture(true));
    when(voteRepository.updateVote(TestData.UPDATE_VOTE_INPUT, "4040")).thenReturn(Future.succeededFuture(null));

    // when
    vertx.eventBus().<Vote>request(Address.VOTE.get(), TestData.UPDATE_VOTE_INPUT, options, event -> {

      // then
      context.verify(() -> {
        assertNull(event.result().body());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_on_error_updating_vote(final Vertx vertx, final VertxTestContext context) {
    // given
    final DeliveryOptions options = new DeliveryOptions().addHeader(USER_ID, TestData.SOME_USER_ID);
    when(voteRepository.updateVote(TestData.UPDATE_VOTE_INPUT, TestData.SOME_USER_ID)).thenReturn(Future.failedFuture(new AssertionError()));
    when(voteRepository
        .hasVoted(new VoteSelector(TestData.UPDATE_VOTE_INPUT.getItemId(), TestData.UPDATE_VOTE_INPUT.getItemType()), TestData.SOME_USER_ID))
        .thenReturn(Future.succeededFuture(true));

    // when
    vertx.eventBus().<Optional<Vote>>request(Address.UPDATE_VOTE.get(), TestData.UPDATE_VOTE_INPUT, options, event -> {

      // then
      context.verify(() -> {
        assertTrue(event.failed());
        context.completeNow();
      });
    });
  }
}
