package io.appwish.voteservice.service;

import static java.util.Objects.isNull;

import io.appwish.grpc.HasVotedReplyProto;
import io.appwish.grpc.UnvoteReplyProto;
import io.appwish.grpc.VoteInputProto;
import io.appwish.grpc.VoteReplyProto;
import io.appwish.grpc.VoteScoreReplyProto;
import io.appwish.grpc.VoteSelectorProto;
import io.appwish.grpc.VoteServiceGrpc;
import io.appwish.voteservice.dto.Score;
import io.appwish.voteservice.dto.Vote;
import io.appwish.voteservice.dto.input.VoteInput;
import io.appwish.voteservice.dto.query.VoteSelector;
import io.appwish.voteservice.dto.reply.UnvoteReply;
import io.appwish.voteservice.dto.reply.VoteReply;
import io.appwish.voteservice.eventbus.Address;
import io.appwish.voteservice.interceptor.UserContextInterceptor;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import net.badata.protobuf.converter.Converter;

/**
 * Handles gRPC server request calls. Sends request on event bus to interact with vote data in the database.
 */
public class GrpcServiceImpl extends VoteServiceGrpc.VoteServiceVertxImplBase {

  private static final String USER_ID = "userId";

  private final EventBus eventBus;
  private final Converter converter;

  public GrpcServiceImpl(final io.vertx.core.eventbus.EventBus eventBus2) {
    this.eventBus = eventBus2;
    this.converter = Converter.create();
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.vote(...)
   */
  @Override
  public void vote(final VoteInputProto input, final Promise<VoteReplyProto> response) {
    final String userId = UserContextInterceptor.USER_CONTEXT.get();
    final DeliveryOptions options = isNull(userId) ? new DeliveryOptions() : new DeliveryOptions().addHeader(USER_ID, userId);

    eventBus.<Vote>request(Address.VOTE.get(), converter.toDomain(VoteInput.class, input), options,
        event -> {
          if (event.succeeded()) {
            response.complete(converter.toProtobuf(VoteReplyProto.class, new VoteReply(event.result().body())));
          } else {
            response.fail(event.cause());
          }
        });
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.unvote(...)
   */
  @Override
  public void unvote(final VoteSelectorProto selector, final Promise<UnvoteReplyProto> response) {
    final String userId = UserContextInterceptor.USER_CONTEXT.get();
    final DeliveryOptions options = isNull(userId) ? new DeliveryOptions() : new DeliveryOptions().addHeader(USER_ID, userId);

    eventBus.<Boolean>request(Address.UNVOTE.get(), converter.toDomain(VoteSelector.class, selector), options,
        event -> {
          if (event.succeeded()) {
            response.complete(converter.toProtobuf(UnvoteReplyProto.class, new UnvoteReply(event.result().body())));
          } else {
            response.fail(event.cause());
          }
        });
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.hasVoted(...)
   */
  @Override
  public void hasVoted(final VoteSelectorProto selector, final Promise<HasVotedReplyProto> response) {
    final String userId = UserContextInterceptor.USER_CONTEXT.get();
    final DeliveryOptions options = isNull(userId) ? new DeliveryOptions() : new DeliveryOptions().addHeader(USER_ID, userId);

    eventBus.<Boolean>request(Address.HAS_VOTED.get(), converter.toDomain(VoteSelector.class, selector), options,
        event -> {
          if (event.succeeded()) {
            response.complete(HasVotedReplyProto.newBuilder().setVoted(event.result().body()).build());
          } else {
            response.fail(event.cause());
          }
        });
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.voteScore(...)
   */
  @Override
  public void voteScore(final VoteSelectorProto selector, final Promise<VoteScoreReplyProto> response) {
    eventBus.<Score>request(Address.VOTE_SCORE.get(), converter.toDomain(VoteSelector.class, selector),
        event -> {
          if (event.succeeded()) {
            response.complete(converter.toProtobuf(VoteScoreReplyProto.class, event.result().body()));
          } else {
            response.fail(event.cause());
          }
        });
  }
}
