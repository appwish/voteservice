package io.appwish.voteservice.verticle;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.appwish.voteservice.TestData;
import io.appwish.voteservice.eventbus.Address;
import io.appwish.voteservice.eventbus.EventBusConfigurer;
import io.appwish.voteservice.repository.VoteRepository;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class DatabaseVerticleTest {

  @Test
  void should_expose_database_service(final Vertx vertx, final VertxTestContext context) {
    // given
    final VoteRepository repository = mock(VoteRepository.class);
    final DatabaseVerticle verticle = new DatabaseVerticle(repository);
    final EventBusConfigurer util = new EventBusConfigurer(vertx.eventBus());
    when(repository.hasVoted(TestData.VOTE_SELECTOR, TestData.SOME_USER_ID)).thenReturn(Future.succeededFuture(false));

    util.registerCodecs();

    // when
    vertx.deployVerticle(verticle, new DeploymentOptions(), context.succeeding());
    vertx.eventBus().<Boolean>request(Address.HAS_VOTED.get(), TestData.VOTE_SELECTOR,
        new DeliveryOptions().addHeader("userId", TestData.SOME_USER_ID), event -> {

          // then
          context.verify(() -> {
            assertTrue(event.succeeded());
            assertFalse(event.result().body());
            context.completeNow();
          });
        });
  }
}
