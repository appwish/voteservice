package io.appwish.voteservice.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import net.badata.protobuf.converter.Converter;
import io.appwish.grpc.AllVoteQueryProto;
import io.appwish.grpc.AllVoteReplyProto;
import io.appwish.grpc.UpdateVoteInputProto;
import io.appwish.grpc.VoteDeleteReplyProto;
import io.appwish.grpc.VoteInputProto;
import io.appwish.grpc.VoteProto;
import io.appwish.grpc.VoteQueryProto;
import io.appwish.grpc.VoteReplyProto;
import io.appwish.grpc.VoteServiceGrpc;
import io.appwish.voteservice.eventbus.Address;
import io.appwish.voteservice.model.Vote;
import io.appwish.voteservice.model.input.UpdateVoteInput;
import io.appwish.voteservice.model.input.VoteInput;
import io.appwish.voteservice.model.query.AllVoteQuery;
import io.appwish.voteservice.model.query.VoteQuery;
import io.appwish.voteservice.model.reply.VoteDeleteReply;
import io.appwish.voteservice.model.reply.VoteReply;

/**
 * Handles gRPC server request calls. Sends request on event bus to interact with wish data in the
 * database.
 */
public class GrpcServiceImpl extends VoteServiceGrpc.VoteServiceVertxImplBase {

  private final EventBus eventBus;
  private final Converter converter;

  public GrpcServiceImpl(final io.vertx.core.eventbus.EventBus eventBus2) {
    this.eventBus = eventBus2;
    this.converter = Converter.create();
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.getVote(...)
   */
  @Override
  public void getVote(final VoteQueryProto request, final Promise<VoteReplyProto> response) {

    eventBus.<Optional<Vote>>request(
      Address.FIND_ONE_VOTE.get(), converter.toDomain(VoteQuery.class, request),
      event -> {
        if (event.succeeded() && event.result().body().isPresent()) {
          response.complete(VoteReplyProto.newBuilder().setVote(converter.toProtobuf(VoteProto.class, event.result().body().get())).build());
        } else if (event.succeeded()) {
          response.complete();
        } else {
          response.fail(event.cause());
        }
      });
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.getAllVote(...)
   */
  @Override
  public void getAllVote(final AllVoteQueryProto request,
    final Promise<AllVoteReplyProto> response) {

    eventBus.<List<Vote>>request(
      Address.FIND_ALL_VOTES.get(), converter.toDomain(AllVoteQuery.class, request),
      event -> {
        if (event.succeeded()) {
          final List<VoteProto> collect = event.result().body().stream()
            .map(it -> converter.toProtobuf(VoteProto.class, it))
            .collect(Collectors.toList());
          response.complete(AllVoteReplyProto.newBuilder().addAllVotes(collect).build());
        } else {
          response.fail(event.cause());
        }
      });
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.addVote(...)
   */
  @Override
  public void createVote(final VoteInputProto request, final Promise<VoteReplyProto> response) {

    eventBus.<Vote>request(
      Address.CREATE_ONE_VOTE.get(), converter.toDomain(VoteInput.class, request),
      event -> {
        if (event.succeeded()) {
          response.complete(converter.toProtobuf(VoteReplyProto.class, new VoteReply(event.result().body())));
        } else {
          response.fail(event.cause());
        }
      });
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.updateVote(...)
   */
  @Override
  public void updateVote(final UpdateVoteInputProto request,
    final Promise<VoteReplyProto> response) {

    eventBus.<Optional<Vote>>request(
      Address.UPDATE_ONE_VOTE.get(), converter.toDomain(UpdateVoteInput.class, request),
      event -> {
        if (event.succeeded() && event.result().body().isPresent()) {
          response.complete(converter.toProtobuf(VoteReplyProto.class, new VoteReply(event.result().body().get())));
        } else if (event.succeeded()) {
          response.complete();
        } else {
          response.fail(event.cause());
        }
      });
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.deleteVote(...)
   */
  @Override
  public void deleteVote(final VoteQueryProto request,
    final Promise<VoteDeleteReplyProto> response) {

    eventBus.<Boolean>request(
      Address.DELETE_ONE_VOTE.get(), converter.toDomain(VoteQuery.class, request),
      event -> {
        if (event.succeeded()) {
          response.complete(converter.toProtobuf(VoteDeleteReplyProto.class, new VoteDeleteReply(event.result().body())));
        } else {
          response.fail(event.cause());
        }
      });
  }
}
