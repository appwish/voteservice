package io.appwish.voteservice.eventbus;

/**
 * Represents addresses available on the event bus
 */
public enum Address {
  FIND_ALL_VOTES,
  FIND_ONE_VOTE,
  CREATE_ONE_VOTE,
  UPDATE_ONE_VOTE,
  DELETE_ONE_VOTE;

  public String get() {
    return name();
  }

}
