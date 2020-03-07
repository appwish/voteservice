package io.appwish.voteservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.List;
import java.util.Optional;

import io.appwish.voteservice.TestData;
import io.vertx.core.Vertx;
import io.appwish.voteservice.eventbus.Address;
import io.appwish.voteservice.eventbus.EventBusConfigurer;
import io.appwish.voteservice.model.Vote;
import io.appwish.voteservice.model.input.UpdateVoteInput;
import io.appwish.voteservice.model.query.AllVoteQuery;
import io.appwish.voteservice.repository.VoteRepository;
import io.appwish.voteservice.service.DatabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class DatabaseServiceTest {

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
  void should_reply_all_votes(final Vertx vertx, final VertxTestContext context) {
    // given
    when(voteRepository.findAll(TestData.ALL_VOTE_QUERY)).thenReturn(Future.succeededFuture(TestData.VOTES));

    // when
    vertx.eventBus().<List<Vote>>request(Address.FIND_ALL_VOTES.get(), TestData.ALL_VOTE_QUERY,
      event -> {

        // then
        context.verify(() -> {
          assertEquals(TestData.VOTES, event.result().body());
          context.completeNow();
        });
      });
  }

  @Test
  void should_return_error_on_error_getting_all_votes(final Vertx vertx, final VertxTestContext context) {
    // given
    when(voteRepository.findAll(TestData.ALL_VOTE_QUERY)).thenReturn(Future.failedFuture(new AssertionError()));

    // when
    vertx.eventBus().<AllVoteQuery>request(Address.FIND_ALL_VOTES.get(), TestData.ALL_VOTE_QUERY,
      event -> {

        // then
        context.verify(() -> {
          assertTrue(event.failed());
          context.completeNow();
        });
      });
  }

  @Test
  void should_reply_added_vote(final Vertx vertx, final VertxTestContext context) {
    // given
    when(voteRepository.addOne(TestData.VOTE_INPUT_1)).thenReturn(Future.succeededFuture(TestData.VOTE_4));

    // when
    vertx.eventBus().<Vote>request(Address.CREATE_ONE_VOTE.get(), TestData.VOTE_INPUT_1, event -> {

      // then
      context.verify(() -> {
        assertEquals(TestData.VOTE_4, event.result().body());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_on_error_adding_vote(final Vertx vertx, final VertxTestContext context) {
    // given
    when(voteRepository.addOne(TestData.VOTE_INPUT_1)).thenReturn(Future.failedFuture(new AssertionError()));

    // when
    vertx.eventBus().<Vote>request(Address.CREATE_ONE_VOTE.get(), TestData.VOTE_INPUT_1, event -> {

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
    when(voteRepository.deleteOne(TestData.VOTE_QUERY)).thenReturn(Future.succeededFuture(true));

    // when
    vertx.eventBus().<Boolean>request(Address.DELETE_ONE_VOTE.get(), TestData.VOTE_QUERY, event -> {

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
    when(voteRepository.deleteOne(TestData.VOTE_QUERY)).thenReturn(Future.succeededFuture(false));

    // when
    vertx.eventBus().<Boolean>request(Address.DELETE_ONE_VOTE.get(),
      TestData.VOTE_QUERY, event -> {

        // then
        context.verify(() -> {
          assertTrue(event.succeeded());
          assertEquals(false, event.result().body());
          context.completeNow();
        });
      });
  }

  @Test
  void should_return_error_on_error_deleting_vote(final Vertx vertx, final VertxTestContext context) {
    // given
    when(voteRepository.deleteOne(TestData.VOTE_QUERY)).thenReturn(Future.failedFuture(new AssertionError()));

    // when
    vertx.eventBus().<Boolean>request(Address.DELETE_ONE_VOTE.get(),
      TestData.VOTE_QUERY, event -> {

        // then
        context.verify(() -> {
          assertTrue(event.failed());
          context.completeNow();
        });
      });
  }

  @Test
  void should_return_found_vote(final Vertx vertx, final VertxTestContext context) {
    // given
    when(voteRepository.findOne(TestData.VOTE_QUERY)).thenReturn(Future.succeededFuture(Optional.of(TestData.VOTE_1)));

    // when
    vertx.eventBus().<Optional<Vote>>request(Address.FIND_ONE_VOTE.get(), TestData.VOTE_QUERY, event -> {

        // then
        context.verify(() -> {
          assertTrue(event.succeeded());
          assertTrue(event.result().body().isPresent());
          assertEquals(TestData.VOTE_1, event.result().body().get());
          context.completeNow();
        });
      });
  }

  @Test
  void should_return_empty_if_vote_not_found(final Vertx vertx, final VertxTestContext context) {
    // given
    when(voteRepository.findOne(TestData.VOTE_QUERY)).thenReturn(Future.succeededFuture(Optional.empty()));

    // when
    vertx.eventBus().<Optional<Vote>>request(Address.FIND_ONE_VOTE.get(),
      TestData.VOTE_QUERY, event -> {

        // then
        context.verify(() -> {
          assertTrue(event.succeeded());
          assertTrue(event.result().body().isEmpty());
          context.completeNow();
        });
      });
  }

  @Test
  void should_return_error_on_error_finding_vote(final Vertx vertx, final VertxTestContext context) {
    // given
    when(voteRepository.findOne(TestData.VOTE_QUERY)).thenReturn(Future.failedFuture(new AssertionError()));

    // when
    vertx.eventBus().<Optional<Vote>>request(Address.FIND_ONE_VOTE.get(),
      TestData.VOTE_QUERY, event -> {

        // then
        context.verify(() -> {
          assertTrue(event.failed());
          context.completeNow();
        });
      });
  }

  @Test
  void should_return_updated_vote(final Vertx vertx, final VertxTestContext context) {
    // given
    final UpdateVoteInput input = TestData.UPDATE_VOTE_INPUT;
    when(voteRepository.updateOne(input)).thenReturn(Future.succeededFuture(Optional.of(TestData.VOTE_4)));

    // when
    vertx.eventBus().<Optional<Vote>>request(Address.UPDATE_ONE_VOTE.get(), input, event -> {

      // then
      context.verify(() -> {
        assertEquals(TestData.VOTE_4, event.result().body().get());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_empty_if_not_updated(final Vertx vertx, final VertxTestContext context) {
    // given
    when(voteRepository.updateOne(TestData.UPDATE_VOTE_INPUT)).thenReturn(Future.succeededFuture(Optional.empty()));

    // when
    vertx.eventBus().<Optional<Vote>>request(Address.UPDATE_ONE_VOTE.get(), TestData.UPDATE_VOTE_INPUT, event -> {

      // then
      context.verify(() -> {
        assertTrue(event.succeeded());
        assertTrue(event.result().body().isEmpty());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_on_error_updating_vote(final Vertx vertx, final VertxTestContext context) {
    // given
    when(voteRepository.updateOne(TestData.UPDATE_VOTE_INPUT)).thenReturn(Future.failedFuture(new AssertionError()));

    // when
    vertx.eventBus().<Optional<Vote>>request(Address.UPDATE_ONE_VOTE.get(), TestData.UPDATE_VOTE_INPUT, event -> {

      // then
      context.verify(() -> {
        assertTrue(event.failed());
        context.completeNow();
      });
    });
  }
}
