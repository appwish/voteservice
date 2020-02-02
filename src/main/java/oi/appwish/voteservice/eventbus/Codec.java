package oi.appwish.voteservice.eventbus;

import io.appwish.voteservice.model.Vote;
import io.appwish.voteservice.model.input.UpdateVoteInput;
import io.appwish.voteservice.model.input.VoteInput;
import io.appwish.voteservice.model.query.AllVoteQuery;
import io.appwish.voteservice.model.query.VoteQuery;
import io.appwish.voteservice.model.reply.AllVoteReply;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageCodec;

/**
 * These codecs can be used to enable passing custom Java objects on the local event bus.
 *
 * To enable T type to be passed via the event bus, just create a new {@link LocalReferenceCodec}.
 *
 * It's not enough to add the codec here - you need to register them on the event bus using {@link
 * EventBus#registerCodec(MessageCodec)}.
 */
public enum Codec {
  UPDATE_WISH_INPUT(new LocalReferenceCodec<>(UpdateVoteInput.class)),
  WISH(new LocalReferenceCodec<>(Vote.class)),
  ALL_WISH_REPLY(new LocalReferenceCodec<>(AllVoteReply.class)),
  ALL_WISH_QUERY(new LocalReferenceCodec<>(AllVoteQuery.class)),
  WISH_QUERY(new LocalReferenceCodec<>(VoteQuery.class)),
  WISH_INPUT(new LocalReferenceCodec<>(VoteInput.class));

  private final LocalReferenceCodec codec;

  Codec(final LocalReferenceCodec codec) {
    this.codec = codec;
  }

  public <T> LocalReferenceCodec<T> getCodec() {
    return codec;
  }

  public String getCodecName() {
    return codec.name();
  }
}
