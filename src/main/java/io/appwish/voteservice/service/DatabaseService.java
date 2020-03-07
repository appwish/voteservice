package io.appwish.voteservice.service;

import io.vertx.core.eventbus.EventBus;
import java.util.List;
import java.util.Optional;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.appwish.voteservice.eventbus.Address;
import io.appwish.voteservice.eventbus.Codec;
import io.appwish.voteservice.model.Vote;
import io.appwish.voteservice.model.input.UpdateVoteInput;
import io.appwish.voteservice.model.input.VoteInput;
import io.appwish.voteservice.model.query.AllVoteQuery;
import io.appwish.voteservice.model.query.VoteQuery;
import io.appwish.voteservice.repository.VoteRepository;

/**
 * Exposes the vote repository on the event bus. Takes data from the vote repository and replies to
 * requests on the event bus.
 */
public class DatabaseService {

  private final EventBus eventBus;
  private final VoteRepository voteRepository;

  public DatabaseService(final EventBus eventBus, final VoteRepository voteRepository) {
    this.eventBus = eventBus;
    this.voteRepository = voteRepository;
  }

  public void registerEventBusEventHandlers() {
	eventBus.<AllVoteQuery>consumer(Address.FIND_ALL_VOTES.get())
      .handler(event -> voteRepository.findAll(event.body()).setHandler(findAllHandler(event)));

    eventBus.<VoteQuery>consumer(Address.FIND_ONE_VOTE.get())
      .handler(event -> voteRepository.findOne(event.body()).setHandler(findOneHandler(event)));

    eventBus.<VoteInput>consumer(Address.CREATE_ONE_VOTE.get())
      .handler(event -> voteRepository.addOne(event.body()).setHandler(addOneHandler(event)));

    eventBus.<UpdateVoteInput>consumer(Address.UPDATE_ONE_VOTE.get())
      .handler(event -> voteRepository.updateOne(event.body()).setHandler(updateOneHandler(event)));

    eventBus.<VoteQuery>consumer(Address.DELETE_ONE_VOTE.get())
      .handler(event -> voteRepository.deleteOne(event.body()).setHandler(deleteOneHandler(event)));
  }

  private Handler<AsyncResult<Boolean>> deleteOneHandler(final Message<VoteQuery> event) {
    return query -> {
      if (query.succeeded()) {
        event.reply(query.result());
      } else {
        event.fail(1, "Could not delete the vote from the database");
      }
    };
  }

  private Handler<AsyncResult<Optional<Vote>>> updateOneHandler(
    final Message<UpdateVoteInput> event) {
    return query -> {
      if (query.succeeded()) {
        event.reply(query.result(), new DeliveryOptions().setCodecName(Codec.VOTE.getCodecName()));
      } else {
        event.fail(1, "Error updating the vote in the database");
      }
    };
  }

  private Handler<AsyncResult<Vote>> addOneHandler(final Message<VoteInput> event) {
    return query -> {
      if (query.succeeded()) {
        event.reply(query.result());
      } else {
        event.fail(1, "Error adding the vote to the database");
      }
    };
  }

  private Handler<AsyncResult<Optional<Vote>>> findOneHandler(final Message<VoteQuery> event) {
    return query -> {
      if (query.succeeded()) {
        event.reply(query.result(), new DeliveryOptions().setCodecName(Codec.VOTE.getCodecName()));
      } else {
        event.fail(1, "Error fetching the vote from the database");
      }
    };
  }

  private Handler<AsyncResult<List<Vote>>> findAllHandler(final Message<AllVoteQuery> event) {
    return query -> {
      if (query.succeeded()) {
        event.reply(query.result(),
          new DeliveryOptions().setCodecName(Codec.VOTE.getCodecName()));
      } else {
        event.fail(1, "Error fetching votes from the database");
      }
    };
  }
}
