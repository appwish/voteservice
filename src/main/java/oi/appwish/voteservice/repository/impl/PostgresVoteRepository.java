package oi.appwish.voteservice.repository.impl;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import oi.appwish.voteservice.model.ItemType;
import oi.appwish.voteservice.model.Vote;
import oi.appwish.voteservice.model.VoteType;
import oi.appwish.voteservice.model.input.UpdateVoteInput;
import oi.appwish.voteservice.model.input.VoteInput;
import oi.appwish.voteservice.model.query.AllVoteQuery;
import oi.appwish.voteservice.model.query.VoteQuery;
import oi.appwish.voteservice.repository.VoteRepository;

/**
 * Enables storing votes in PostgreSQL
 */
public class PostgresVoteRepository implements VoteRepository {

  private static final String ID_COLUMN = "id";
  private static final String USER_ID_COLUMN = "userId";
  private static final String ITEM_ID_COLUMN = "itemId";
  private static final String ITEM_TYPE_COLUMN = "itemType";
  private static final String CREATED_AT_COLUMN = "createdAt";
  private static final String VOTE_TYPE_COLUMN = "voteType";

  private final PgPool client;

  public PostgresVoteRepository(final PgPool client) {
    this.client = client;
  }

  @Override
  public Future<List<Vote>> findAll(final AllVoteQuery query) {
    final Promise<List<Vote>> promise = Promise.promise();

    client.preparedQuery(Query.FIND_ALL_VOTE.sql(), event -> {
      if (event.succeeded()) {
        final List<Vote> votes = StreamSupport
          .stream(event.result().spliterator(), false)
          .map(this::voteFromRow)
          .collect(Collectors.toList());
        promise.complete(votes);
      } else {
        promise.fail(event.cause());
      }
    });

    return promise.future();
  }

  @Override
  public Future<Optional<Vote>> findOne(final VoteQuery query) {
    final Promise<Optional<Vote>> promise = Promise.promise();

    client.preparedQuery(Query.FIND_ONE_VOTE.sql(), Tuple.of(query.getId()), event -> {
      if (event.succeeded()) {
        if (event.result().iterator().hasNext()) {
          final Row firstRow = event.result().iterator().next();
          final Vote vote = voteFromRow(firstRow);
          promise.complete(Optional.of(vote));
        } else {
          promise.complete(Optional.empty());
        }
      } else {
        promise.fail(event.cause());
      }
    });

    return promise.future();
  }

  @Override
  public Future<Vote> addOne(final VoteInput vote) {
    final Promise<Vote> promise = Promise.promise();

    final Random random = new Random(); // TODO remove hardcoded values
    client.preparedQuery(Query.INSERT_VOTE_QUERY.sql(),
      Tuple.of(vote.getUserId(), vote.getItemId(), vote.getItemType(), vote.getCreatedAt(), vote.getVoteType()),
      event -> {
        if (event.succeeded()) {
          if (event.result().iterator().hasNext()) {
            final Row row = event.result().iterator().next();
            promise.complete(voteFromRow(row));
          } else {
            promise.fail(new AssertionError("Adding a vote should always succeed"));
          }
        } else {
          promise.fail(event.cause());
        }
      });

    return promise.future();
  }

  @Override
  public Future<Boolean> deleteOne(final VoteQuery query) {
    final Promise<Boolean> promise = Promise.promise();

    client.preparedQuery(Query.DELETE_VOTE_QUERY.sql(), Tuple.of(query.getId()), event -> {
      if (event.succeeded()) {
        if (event.result().rowCount() == 1) {
          promise.complete(true);
        } else {
          promise.complete(false);
        }
      } else {
        promise.fail(event.cause());
      }
    });

    return promise.future();
  }

  @Override
  public Future<Optional<Vote>> updateOne(final UpdateVoteInput vote) {
    final Promise<Optional<Vote>> promise = Promise.promise();

    client.preparedQuery(Query.UPDATE_VOTE_QUERY.sql(),
      Tuple.of(vote.getUserId(), vote.getItemId(), vote.getItemType(), vote.getCreatedAt(), vote.getVoteType()),
      event -> {
        if (event.succeeded() && event.result().rowCount() == 1) {
          final Row row = event.result().iterator().next();
          promise.complete(Optional.of(voteFromRow(row)));
        } else if (event.succeeded() && event.result().rowCount() == 0) {
          promise.complete(Optional.empty());
        } else if (event.failed()) {
          promise.fail(event.cause());
        }
      });

    return promise.future();
  }

  private Vote voteFromRow(final Row row) {
    return new Vote(
      row.getLong(ID_COLUMN),
      row.getLong(USER_ID_COLUMN),
      row.getLong(ITEM_ID_COLUMN),
      ItemType.valueOf(row.getString(ITEM_TYPE_COLUMN)),
      row.getLocalDateTime(CREATED_AT_COLUMN),
      VoteType.valueOf(row.getString(VOTE_TYPE_COLUMN)));
  }
}
