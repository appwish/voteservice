package oi.appwish.voteservice.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import oi.appwish.voteservice.repository.VoteRepository;
import oi.appwish.voteservice.service.DatabaseService;

/**
 * Verticle responsible for database access. Registers DatabaseService to expose the database on the
 * event bus.
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
