package io.appwish.voteservice.repository.impl;

import com.google.protobuf.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import io.appwish.voteservice.model.ItemType;
import io.appwish.voteservice.model.Vote;
import io.appwish.voteservice.model.VoteType;
import io.appwish.voteservice.model.input.UpdateVoteInput;
import io.appwish.voteservice.model.input.VoteInput;
import io.appwish.voteservice.model.query.AllVoteQuery;
import io.appwish.voteservice.model.query.VoteQuery;
import io.appwish.voteservice.repository.VoteRepository;

/**
 * Enables storing votes in PostgreSQL
 */
public class PostgresVoteRepository implements VoteRepository {

  private static final String ID_COLUMN = "id";
  private static final String USER_ID_COLUMN = "user_id";
  private static final String ITEM_ID_COLUMN = "item_id";
  private static final String ITEM_TYPE_COLUMN = "item_type";
  private static final String CREATED_AT_COLUMN = "created_at";
  private static final String VOTE_TYPE_COLUMN = "vote_type";

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

    client.preparedQuery(Query.INSERT_VOTE_QUERY.sql(),
      Tuple.of(vote.getUserId(), vote.getItemId(), vote.getItemType().toString(), vote.getVoteType().toString(), LocalDateTime.now()),
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
      // TODO remove toString()
      Tuple.of(vote.getItemId(), vote.getVoteType().toString()),
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
    final LocalDateTime createdAt = row.getLocalDateTime(CREATED_AT_COLUMN);

    return new Vote(
      row.getLong(ID_COLUMN),
      row.getLong(USER_ID_COLUMN),
      row.getLong(ITEM_ID_COLUMN),
      ItemType.valueOf(row.getString(ITEM_TYPE_COLUMN)),
      VoteType.valueOf(row.getString(VOTE_TYPE_COLUMN)),
      Timestamp.newBuilder().setNanos(createdAt.toInstant(ZoneOffset.UTC).getNano()).setSeconds(createdAt.toInstant(ZoneOffset.UTC).getEpochSecond()).build());
  }
}
