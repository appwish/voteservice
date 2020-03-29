package io.appwish.voteservice.service;

import static java.util.Objects.isNull;

import io.appwish.voteservice.dto.Vote;
import io.appwish.voteservice.dto.input.VoteInput;
import io.appwish.voteservice.dto.query.VoteSelector;
import io.appwish.voteservice.eventbus.Address;
import io.appwish.voteservice.repository.VoteRepository;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

/**
 * Exposes the vote repository on the event bus. Takes data from the vote repository and replies to requests on the event bus.
 */
public class DatabaseService {

  private static final String USER_ID = "userId";

  private final EventBus eventBus;
  private final VoteRepository voteRepository;

  public DatabaseService(final EventBus eventBus, final VoteRepository voteRepository) {
    this.eventBus = eventBus;
    this.voteRepository = voteRepository;
  }

  public void registerEventBusEventHandlers() {
    eventBus.<VoteInput>consumer(Address.VOTE.get())
        .handler(event -> {
          final String userId = event.headers().get(USER_ID);

          if (isNull(userId)) {
            event.fail(1, "User needs to be logged in to vote");
            return;
          }

          final VoteSelector selector = new VoteSelector(event.body().getItemId(), event.body().getItemType());

          voteRepository.hasVoted(selector, userId)
              .onSuccess(voted -> {
                if (!voted) {
                  voteRepository.vote(event.body(), userId).setHandler(voteHandler(event));
                } else {
                  final VoteInput input = new VoteInput(event.body().getItemId(), event.body().getItemType(), event.body().getVoteType());
                  // TODO create separate handler
                  voteRepository.updateVote(input, userId).setHandler(voteHandler(event));
                }
              })
              .onFailure(failure -> event.fail(1, failure.getMessage()));
        });

    eventBus.<VoteSelector>consumer(Address.UNVOTE.get())
        .handler(event -> {
          final String userId = event.headers().get(USER_ID);

          if (isNull(userId)) {
            event.fail(1, "User needs to be authenticated to unvote");
            return;
          }

          voteRepository.unvote(event.body(), userId).setHandler(unvoteHandler(event));
        });

    eventBus.<VoteSelector>consumer(Address.HAS_VOTED.get())
        .handler(event -> {
          final String userId = event.headers().get(USER_ID);

          if (isNull(userId)) {
            event.fail(1, "To check if voted, user needs to be authenticated");
            return;
          }

          // TODO create separate handler
          voteRepository.hasVoted(
              new VoteSelector(event.body().getItemId(), event.body().getItemType()), userId)
              .onSuccess(event::reply)
              .onFailure(f -> event.fail(1, f.getMessage()));
        });

    eventBus.<VoteSelector>consumer(Address.VOTE_SCORE.get())
        .handler(event -> {
          // TODO create separate handler
          voteRepository.voteScore(event.body())
              .onSuccess(event::reply)
              .onFailure(f -> event.fail(1, f.getMessage()));
        });
  }

  private Handler<AsyncResult<Vote>> voteHandler(final Message<VoteInput> event) {
    return query -> {
      if (query.succeeded()) {
        event.reply(query.result());
      } else {
        event.fail(1, query.cause().getMessage());
      }
    };
  }

  private Handler<AsyncResult<Boolean>> unvoteHandler(final Message<VoteSelector> event) {
    return query -> {
      if (query.succeeded()) {
        event.reply(query.result());
      } else {
        event.fail(1, query.cause().getMessage());
      }
    };
  }
}
