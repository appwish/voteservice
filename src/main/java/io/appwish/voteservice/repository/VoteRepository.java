package io.appwish.voteservice.repository;

import io.appwish.voteservice.dto.Score;
import io.appwish.voteservice.dto.Vote;
import io.appwish.voteservice.dto.input.VoteInput;
import io.appwish.voteservice.dto.query.VoteSelector;
import io.vertx.core.Future;

/**
 * Interface for interaction with vote persistence layer
 */
public interface VoteRepository {

  /**
   * Persists user's vote in the database.
   */
  Future<Vote> vote(final VoteInput input, final String userId);

  /**
   * Updates user's vote in the database.
   */
  Future<Vote> updateVote(final VoteInput input, final String userId);

  /**
   * Removes users vote from the database.
   */
  Future<Boolean> unvote(final VoteSelector selector, final String userId);

  /**
   * Checks if user has already voted on given item.
   */
  Future<Boolean> hasVoted(final VoteSelector selector, final String userId);

  /**
   * Returns the score of given item.
   */
  Future<Score> voteScore(final VoteSelector selector);
}
