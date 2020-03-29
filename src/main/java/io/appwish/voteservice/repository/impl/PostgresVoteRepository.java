package io.appwish.voteservice.repository.impl;

import com.google.protobuf.Timestamp;
import io.appwish.voteservice.model.Score;
import io.appwish.voteservice.model.Vote;
import io.appwish.voteservice.model.input.VoteInput;
import io.appwish.voteservice.model.query.VoteSelector;
import io.appwish.voteservice.model.type.ItemType;
import io.appwish.voteservice.model.type.VoteType;
import io.appwish.voteservice.repository.VoteRepository;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
  private static final String SCORE = "score";
  private static final String UP = "UP";
  private static final String DOWN = "DOWN";

  private final PgPool client;

  public PostgresVoteRepository(final PgPool client) {
    this.client = client;
  }

  @Override
  public Future<Vote> vote(final VoteInput input, final String userId) {
    final Promise<Vote> promise = Promise.promise();

    client.preparedQuery(Query.VOTE.sql(),
        Tuple.of(userId, input.getItemId(), input.getItemType().toString(), input.getVoteType().toString(), LocalDateTime.now()),
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
  public Future<Vote> updateVote(final VoteInput input, final String userId) {
    final Promise<Vote> promise = Promise.promise();

    client.preparedQuery(Query.UPDATE_VOTE.sql(),
        Tuple.of(input.getVoteType().toString(), input.getItemId(), input.getItemType().toString(), userId),
        event -> {
          if (event.succeeded()) {
            if (event.result().iterator().hasNext()) {
              final Row row = event.result().iterator().next();
              promise.complete(voteFromRow(row));
            } else {
              // Vote to update has not been found
              promise.complete(null);
            }
          } else {
            promise.fail(event.cause());
          }
        });

    return promise.future();

  }

  @Override
  public Future<Boolean> unvote(final VoteSelector query, final String userId) {
    final Promise<Boolean> promise = Promise.promise();

    client.preparedQuery(Query.UNVOTE.sql(), Tuple.of(query.getItemId(), query.getItemType().toString(), userId), event -> {
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
  public Future<Boolean> hasVoted(final VoteSelector selector, final String userId) {
    final Promise<Boolean> promise = Promise.promise();

    client.preparedQuery(Query.HAS_VOTED.sql(), Tuple.of(userId, selector.getItemId(), selector.getItemType().toString()), event -> {
      if (event.succeeded()) {
        if (event.result().iterator().hasNext()) {
          // TODO think of returning vote_type, too
          final Row row = event.result().iterator().next();
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
  public Future<Score> voteScore(final VoteSelector selector) {
    final Promise<Score> promise = Promise.promise();

    client.preparedQuery(Query.GET_SCORE.sql(), Tuple.of(selector.getItemId(), selector.getItemType().toString()), event -> {
      if (event.succeeded()) {
        long up = 0;
        long down = 0;

        for (Row row : event.result()) {
          final String voteType = row.getString(VOTE_TYPE_COLUMN);

          switch (voteType) {
            case UP:
              up = row.getLong(SCORE);
              break;
            case DOWN:
              down = row.getLong(SCORE);
              break;
          }
        }

        promise.complete(new Score(up, down));
      } else {
        promise.fail(event.cause());
      }
    });

    return promise.future();
  }

  private Vote voteFromRow(final Row row) {
    final LocalDateTime createdAt = row.getLocalDateTime(CREATED_AT_COLUMN);
    return new Vote(
        row.getLong(ID_COLUMN),
        row.getString(USER_ID_COLUMN),
        row.getLong(ITEM_ID_COLUMN),
        ItemType.valueOf(row.getString(ITEM_TYPE_COLUMN)),
        VoteType.valueOf(row.getString(VOTE_TYPE_COLUMN)),
        Timestamp.newBuilder().setNanos(createdAt.toInstant(ZoneOffset.UTC).getNano())
            .setSeconds(createdAt.toInstant(ZoneOffset.UTC).getEpochSecond()).build());
  }
}
