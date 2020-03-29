package io.appwish.voteservice.eventbus;


import io.appwish.voteservice.model.Score;
import io.appwish.voteservice.model.Vote;
import io.appwish.voteservice.model.input.VoteInput;
import io.appwish.voteservice.model.query.VoteSelector;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageCodec;

/**
 * These codecs can be used to enable passing custom Java objects on the local event bus.
 * <p>
 * To enable T type to be passed via the event bus, just create a new {@link LocalReferenceCodec}.
 * <p>
 * It's not enough to add the codec here - you need to register them on the event bus using {@link EventBus#registerCodec(MessageCodec)}.
 */
public enum Codec {
  VOTE(new LocalReferenceCodec<>(Vote.class)),
  VOTE_INPUT(new LocalReferenceCodec<>(VoteInput.class)),
  VOTE_SELECTOR(new LocalReferenceCodec<>(VoteSelector.class)),
  SCORE(new LocalReferenceCodec<>(Score.class));

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
