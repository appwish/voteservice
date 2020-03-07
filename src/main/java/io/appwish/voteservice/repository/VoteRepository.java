package io.appwish.voteservice.repository;

import io.vertx.core.Future;
import java.util.List;
import java.util.Optional;

import io.appwish.voteservice.model.Vote;
import io.appwish.voteservice.model.input.UpdateVoteInput;
import io.appwish.voteservice.model.input.VoteInput;
import io.appwish.voteservice.model.query.AllVoteQuery;
import io.appwish.voteservice.model.query.VoteQuery;

/**
 * Interface for interaction with vote persistence layer
 */
public interface VoteRepository {

  Future<List<Vote>> findAll(final AllVoteQuery query);

  Future<Optional<Vote>> findOne(final VoteQuery query);

  Future<Vote> addOne(final VoteInput input);

  Future<Boolean> deleteOne(final VoteQuery query);

  Future<Optional<Vote>> updateOne(final UpdateVoteInput input);
}
