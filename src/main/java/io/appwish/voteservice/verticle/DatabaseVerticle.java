package io.appwish.voteservice.verticle;

import io.appwish.voteservice.repository.VoteRepository;
import io.appwish.voteservice.service.DatabaseService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

/**
 * Verticle responsible for database access. Registers DatabaseService to expose the database on the event bus.
 */
public class DatabaseVerticle extends AbstractVerticle {

  private final VoteRepository voteRepository;

  public DatabaseVerticle(final VoteRepository voteRepository) {
    this.voteRepository = voteRepository;
  }

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    final DatabaseService databaseService = new DatabaseService(vertx.eventBus(), voteRepository);
    databaseService.registerEventBusEventHandlers();
    startPromise.complete();
  }
}
