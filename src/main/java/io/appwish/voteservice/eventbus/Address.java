package io.appwish.voteservice.eventbus;

/**
 * Represents addresses available on the event bus
 */
public enum Address {
  VOTE,
  UPDATE_VOTE,
  UNVOTE,
  HAS_VOTED,
  VOTE_SCORE;

  public String get() {
    return name();
  }

}
