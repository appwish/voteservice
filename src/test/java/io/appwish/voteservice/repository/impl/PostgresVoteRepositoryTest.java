package io.appwish.voteservice.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.appwish.voteservice.TestData;
import io.appwish.voteservice.model.Score;
import io.appwish.voteservice.model.Vote;
import io.appwish.voteservice.model.input.VoteInput;
import io.appwish.voteservice.model.query.VoteSelector;
import io.appwish.voteservice.model.type.ItemType;
import io.appwish.voteservice.model.type.VoteType;
import io.appwish.voteservice.repository.VoteRepository;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class PostgresVoteRepositoryTest {

  private static final String DATABASE_HOST = "localhost";
  private static final String DEFAULT_POSTGRES = "postgres";

  private EmbeddedPostgres postgres;
  private VoteRepository repository;

  @BeforeEach
  void setUp(final Vertx vertx, final VertxTestContext context) throws Exception {
    postgres = EmbeddedPostgres.start();

    final PgConnectOptions connectOptions = new PgConnectOptions()
        .setPort(postgres.getPort())
        .setHost(DATABASE_HOST)
        .setDatabase(DEFAULT_POSTGRES)
        .setUser(DEFAULT_POSTGRES)
        .setPassword(DEFAULT_POSTGRES);
    final PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
    final PgPool client = PgPool.pool(connectOptions, poolOptions);

    client.query(Query.CREATE_VOTE_TABLE.sql(), context.completing());

    repository = new PostgresVoteRepository(client);
  }

  @AfterEach
  void tearDown() throws Exception {
    postgres.close();
  }

  @Test
  void should_be_able_to_store_vote(final Vertx vertx, final VertxTestContext context) {
    // given
    final VoteInput wishInput = new VoteInput(
        TestData.SOME_ITEM_ID,
        TestData.SOME_ITEM_TYPE,
        TestData.SOME_VOTE_TYPE);

    // when
    repository.vote(wishInput, TestData.SOME_USER_ID)
        .setHandler(event -> {

          // then
          context.verify(() -> {
            assertTrue(event.succeeded());
            assertEquals(TestData.SOME_USER_ID, event.result().getUserId());
            assertEquals(TestData.SOME_ITEM_ID, event.result().getItemId());
            assertEquals(TestData.SOME_ITEM_TYPE, event.result().getItemType());
            assertEquals(TestData.SOME_VOTE_TYPE, event.result().getVoteType());
            context.completeNow();
          });
        });
  }

  @Test
  void should_not_delete_non_existing_vote(final Vertx vertx, final VertxTestContext context) {
    // when
    repository.unvote(new VoteSelector(TestData.NON_EXISTING_ID, ItemType.COMMENT), TestData.SOME_USER_ID)
        .setHandler(event -> {

          // then
          context.verify(() -> {
            assertTrue(event.succeeded());
            assertFalse(event.result());
            context.completeNow();
          });
        });
  }

  @Test
  void should_be_able_to_delete_existing_vote(final Vertx vertx, final VertxTestContext context) {
    // given
    context.assertComplete(repository.vote(TestData.VOTE_INPUT_1, TestData.SOME_USER_ID)).setHandler(event -> {
      final long id = event.result().getId();

      // when
      repository.unvote(new VoteSelector(TestData.VOTE_INPUT_1.getItemId(), TestData.VOTE_INPUT_1.getItemType()), TestData.SOME_USER_ID)
          .setHandler(query -> {

            // then
            context.verify(() -> {
              assertTrue(query.succeeded());
              assertTrue(query.result());
              context.completeNow();
            });
          });
    });
  }

  @Test
  void should_not_update_non_existing_vote(final Vertx vertx, final VertxTestContext context) {
    // given
    final VoteInput updated = new VoteInput(
        TestData.VOTE_2.getItemId(),
        TestData.VOTE_2.getItemType(),
        TestData.VOTE_2.getVoteType());

    // when
    repository.updateVote(updated, TestData.SOME_USER_ID).setHandler(query -> {

      // then
      context.verify(() -> {
        assertTrue(query.succeeded());
        assertNull(query.result());
        context.completeNow();
      });
    });
  }

  @Test
  void should_update_existing_vote(final Vertx vertx, final VertxTestContext context) throws Exception {
    // given
    context.assertComplete(repository.vote(TestData.VOTE_INPUT_1, TestData.SOME_USER_ID)).setHandler(event -> {
      final VoteInput updated = new VoteInput(
          TestData.VOTE_1.getItemId(),
          TestData.VOTE_1.getItemType(),
          VoteType.DOWN);

      // when
      repository.updateVote(updated, TestData.SOME_USER_ID).setHandler(query -> {

        // then
        context.verify(() -> {
          assertTrue(query.succeeded());
          assertNotNull(query.result());
          assertEquals(VoteType.DOWN, query.result().getVoteType());
          assertEquals(TestData.SOME_USER_ID, query.result().getUserId());
          context.completeNow();
        });
      });
    });
  }

  @Test
  void should_calculate_vote_score_correctly(final Vertx vertx, final VertxTestContext context) {
    // given
    Future<Vote> vote1 = context
        .assertComplete(repository.vote(new VoteInput(TestData.SOME_ITEM_ID, TestData.SOME_ITEM_TYPE, VoteType.UP), TestData.SOME_USER_ID));
    Future<Vote> vote2 = context
        .assertComplete(repository.vote(new VoteInput(TestData.SOME_ITEM_ID, TestData.SOME_ITEM_TYPE, VoteType.UP), TestData.SOME_USER_ID));
    Future<Vote> vote3 = context
        .assertComplete(repository.vote(new VoteInput(TestData.SOME_ITEM_ID, TestData.SOME_ITEM_TYPE, VoteType.UP), TestData.SOME_USER_ID));
    Future<Vote> vote4 = context
        .assertComplete(repository.vote(new VoteInput(TestData.SOME_ITEM_ID, TestData.SOME_ITEM_TYPE, VoteType.UP), "anotherUser"));
    Future<Vote> vote5 = context
        .assertComplete(repository.vote(new VoteInput(TestData.SOME_ITEM_ID, TestData.SOME_ITEM_TYPE, VoteType.DOWN), "anotherUser"));
    Future<Vote> vote6 = context
        .assertComplete(repository.vote(new VoteInput(TestData.SOME_ITEM_ID, TestData.SOME_ITEM_TYPE, VoteType.DOWN), "anotherUser"));
    CompositeFuture.all(vote1, vote2, vote3, vote4, vote5, vote6).onSuccess(event -> {

      // when
      repository.voteScore(new VoteSelector(TestData.SOME_ITEM_ID, TestData.SOME_ITEM_TYPE))
          .onFailure(context::failNow)
          .onSuccess(res -> {

            // then
            context.verify(() -> {
              assertEquals(4, res.getUp());
              assertEquals(2, res.getDown());
              assertEquals(2, res.getScore());
              context.completeNow();
            });
          });
    });
  }

  @Test
  @Timeout(value = 5, timeUnit = TimeUnit.SECONDS)
  void should_fail_fast_on_postgres_connection_error(final Vertx vertx, final VertxTestContext context) throws Exception {
    // given
    final VoteSelector selector = new VoteSelector(123L, ItemType.COMMENT);
    final VoteInput updateVoteInput = new VoteInput(
        TestData.SOME_ITEM_ID,
        TestData.SOME_ITEM_TYPE,
        TestData.SOME_VOTE_TYPE);

    // database down
    postgres.close();

    // when
    final Future<Vote> vote = repository.vote(TestData.VOTE_INPUT_1, TestData.SOME_USER_ID);
    final Future<Boolean> hasVoted = repository.hasVoted(selector, TestData.SOME_USER_ID);
    final Future<Boolean> unvote = repository.unvote(selector, TestData.SOME_USER_ID);
    final Future<Vote> updateVote = repository.updateVote(updateVoteInput, TestData.SOME_USER_ID);
    final Future<Score> score = repository.voteScore(selector);

    // then
    CompositeFuture.any(vote, hasVoted, unvote, updateVote, score).setHandler(event -> {
      if (event.succeeded()) {
        context.failNow(new AssertionError("All queries should fail!"));
      } else {
        context.completeNow();
      }
    });
  }
}
