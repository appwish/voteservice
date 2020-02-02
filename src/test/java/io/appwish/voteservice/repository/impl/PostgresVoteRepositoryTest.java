package io.appwish.voteservice.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import io.appwish.voteservice.TestData;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Vertx;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import oi.appwish.voteservice.model.Vote;
import oi.appwish.voteservice.model.input.UpdateVoteInput;
import oi.appwish.voteservice.model.input.VoteInput;
import oi.appwish.voteservice.model.query.AllVoteQuery;
import oi.appwish.voteservice.model.query.VoteQuery;
import oi.appwish.voteservice.repository.VoteRepository;
import oi.appwish.voteservice.repository.impl.PostgresVoteRepository;
import oi.appwish.voteservice.repository.impl.Query;

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
  void should_be_able_to_store_wish(final Vertx vertx, final VertxTestContext context) {
    // given
    final VoteInput wishInput = new VoteInput(
      TestData.SOME_TITLE,
      TestData.SOME_CONTENT,
      TestData.SOME_COVER_IMAGE_URL);

    // when
    repository.addOne(wishInput)
      .setHandler(event -> {

        // then
        context.verify(() -> {
          assertTrue(event.succeeded());
          assertEquals(TestData.SOME_TITLE, event.result().getTitle());
          assertEquals(TestData.SOME_CONTENT, event.result().getContent());
          assertEquals(TestData.SOME_COVER_IMAGE_URL, event.result().getCoverImageUrl());
          context.completeNow();
        });
      });
  }

  @Test
  void should_be_able_to_read_wish(final Vertx vertx, final VertxTestContext context) {
    // given
    final VoteInput wishInput = new VoteInput(
      TestData.SOME_TITLE,
      TestData.SOME_CONTENT,
      TestData.SOME_COVER_IMAGE_URL);
    context.assertComplete(repository.addOne(wishInput)).setHandler(event -> {

      // when
      repository.findOne(new VoteQuery(event.result().getId())).setHandler(query -> {

        // then
        context.verify(() -> {
          assertTrue(query.succeeded());
          assertTrue(query.result().isPresent());
          assertEquals(TestData.SOME_TITLE, query.result().get().getTitle());
          assertEquals(TestData.SOME_CONTENT, query.result().get().getContent());
          assertEquals(TestData.SOME_COVER_IMAGE_URL, query.result().get().getCoverImageUrl());
          context.completeNow();
        });
      });
    });
  }

  @Test
  void should_be_able_to_read_multiple_wishes(final Vertx vertx, final VertxTestContext context) {
    // given
    final Future<Vote> addVote1 = repository.addOne(TestData.VOTE_INPUT_1);
    final Future<Vote> addVote2 = repository.addOne(TestData.VOTE_INPUT_2);
    final Future<Vote> addVote3 = repository.addOne(TestData.VOTE_INPUT_3);
    final Future<Vote> addVote4 = repository.addOne(TestData.VOTE_INPUT_4);
    context.assertComplete(CompositeFuture.all(addVote1, addVote2, addVote3, addVote4))
      .setHandler(event -> {

        // when
        repository.findAll(new AllVoteQuery()).setHandler(query -> context.verify(() -> {

          // then
          assertTrue(query.succeeded());
          assertEquals(4, query.result().size());
          query.result().forEach(wish -> assertTrue(isInList(wish, TestData.VOTEES)));
          context.completeNow();
        }));
      });
  }

  @Test
  void should_not_delete_non_existent_wish(final Vertx vertx, final VertxTestContext context) {
    // when
    repository.deleteOne(new VoteQuery(TestData.NON_EXISTING_ID))
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
  void should_be_able_to_delete_existing_wish(final Vertx vertx, final VertxTestContext context) {
    // given
    context.assertComplete(repository.addOne(TestData.VOTE_INPUT_1)).setHandler(event -> {
      final long id = event.result().getId();

      // when
      repository.deleteOne(new VoteQuery(id)).setHandler(query -> {

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
  void should_not_update_non_existent_wish(final Vertx vertx, final VertxTestContext context) {
    // given
    final UpdateVoteInput updated = new UpdateVoteInput(
      TestData.NON_EXISTING_ID,
      TestData.VOTE_2.getTitle(), TestData.VOTE_2.getContent(),
      TestData.VOTE_2.getCoverImageUrl());

    // when
    repository.updateOne(updated).setHandler(query -> {

      // then
      context.verify(() -> {
        assertTrue(query.succeeded());
        assertTrue(query.result().isEmpty());
        context.completeNow();
      });
    });
  }

  @Test
  void  should_update_existing_wish(final Vertx vertx, final VertxTestContext context)
    throws Exception {
    // given
    context.assertComplete(repository.addOne(TestData.VOTE_INPUT_1)).setHandler(event -> {
      final long id = event.result().getId();
      final UpdateVoteInput updated = new UpdateVoteInput(id, TestData.VOTE_2.getTitle(),
        TestData.VOTE_1.getContent(), TestData.VOTE_1.getCoverImageUrl());

      // when
      repository.updateOne(updated).setHandler(query -> {

        // then
        context.verify(() -> {
          assertTrue(query.succeeded());
          assertTrue(query.result().isPresent());
          assertEquals(TestData.VOTE_2.getTitle(), query.result().get().getTitle());
          assertEquals(TestData.VOTE_1.getContent(), query.result().get().getContent());
          assertEquals(TestData.VOTE_1.getCoverImageUrl(), query.result().get().getCoverImageUrl());
          assertEquals(id, query.result().get().getId());
          context.completeNow();
        });
      });
    });
  }

  @Test
  @Timeout(value = 5, timeUnit = TimeUnit.SECONDS)
  void should_fail_fast_on_postgres_connection_error(final Vertx vertx,
    final VertxTestContext context) throws Exception {
    // given
    final UpdateVoteInput updateVoteInput = new UpdateVoteInput(
      TestData.SOME_ID,
      TestData.SOME_TITLE,
      TestData.SOME_CONTENT,
      TestData.SOME_COVER_IMAGE_URL);

    // database down
    postgres.close();

    // when
    final Future<Vote> addVote = repository.addOne(TestData.VOTE_INPUT_1);
    final Future<List<Vote>> findAllVotees = repository.findAll(new AllVoteQuery());
    final Future<Optional<Vote>> findOneVote = repository.findOne(new VoteQuery(TestData.SOME_ID));
    final Future<Optional<Vote>> updateVote = repository.updateOne(updateVoteInput);

    // then
    CompositeFuture.any(addVote, findAllVotees, findOneVote, updateVote).setHandler(event -> {
      if (event.succeeded()) {
        context.failNow(new AssertionError("All queries should fail!"));
      } else {
        context.completeNow();
      }
    });
  }

  private static boolean isInList(final Vote wish, final List<Vote> list) {
    return list.stream().anyMatch(wishFromList ->
      wish.getContent().equals(wishFromList.getContent()) &&
        wish.getTitle().equals(wishFromList.getTitle()) &&
        wish.getCoverImageUrl().equals(wishFromList.getCoverImageUrl()));
  }
}
