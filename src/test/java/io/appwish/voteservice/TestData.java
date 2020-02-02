package io.appwish.voteservice;

import java.util.List;

import oi.appwish.voteservice.model.Vote;
import oi.appwish.voteservice.model.input.UpdateVoteInput;
import oi.appwish.voteservice.model.input.VoteInput;
import oi.appwish.voteservice.model.query.AllVoteQuery;
import oi.appwish.voteservice.model.query.VoteQuery;

/**
 * Class for constant test data/values to be used in test classes to avoid duplication /
 * boilerplate
 */
public final class TestData {

  /**
   * Represents app address that should be used during the tests
   */
  public static final String APP_HOST = "localhost";

  /**
   * Represents app port that should be used during the tests to avoid ports conflicts
   */
  public static final int APP_PORT = 8281;

  /**
   * Some random values to be used to fill Vote fields in tests
   * */
  public static final long SOME_ID = 1;
  public static final long SOME_AUTHOR_ID = 9999;
  public static final String SOME_TITLE = "Title1";
  public static final String SOME_CONTENT = "# Gimme the app!";
  public static final String SOME_COVER_IMAGE_URL = "https://appvote.org/static/hardcoded";
  public static final String SOME_VOTE_URL = "https://appvote.org/vote/hardcoded";

  /**
   * Some random error message
   */
  public static final String ERROR_MESSAGE = "Something went wrong";

  /**
   * Use this in test for IDs that you assume do not exist in database
   */
  public static final long NON_EXISTING_ID = 1411223L;

  /**
   * Votees to be reused in tests
   */
  public static final Vote VOTE_1 = new Vote(SOME_ID, SOME_TITLE, SOME_CONTENT, SOME_COVER_IMAGE_URL, SOME_AUTHOR_ID, SOME_VOTE_URL);
  public static final Vote VOTE_2 = new Vote(2, "title2", "desc2", "url2", 92, "posturl2");
  public static final Vote VOTE_3 = new Vote(3, "title3", "desc3", "url3", 93, "posturl3");
  public static final Vote VOTE_4 = new Vote(4, "title4", "desc4", "url4", 94, "posturl4");

  /**
   * List of random votees to be used in tests
   */
  public static final List<Vote> VOTEES = List.of(VOTE_1, VOTE_2, VOTE_3, VOTE_4);

  /**
   * All vote query to be used in tests
   */
  public static final AllVoteQuery ALL_VOTE_QUERY = new AllVoteQuery();

  /**
   * Some random inputs to be used in tests
   */
  public static final VoteInput VOTE_INPUT_1 = new VoteInput(
    TestData.VOTE_1.getTitle(),
    TestData.VOTE_1.getContent(),
    TestData.VOTE_1.getCoverImageUrl());
  public static final VoteInput VOTE_INPUT_2 = new VoteInput(
    TestData.VOTE_2.getTitle(),
    TestData.VOTE_2.getContent(),
    TestData.VOTE_2.getCoverImageUrl());
  public static final VoteInput VOTE_INPUT_3 = new VoteInput(
    TestData.VOTE_3.getTitle(),
    TestData.VOTE_3.getContent(),
    TestData.VOTE_3.getCoverImageUrl());
  public static final VoteInput VOTE_INPUT_4 = new VoteInput(
    TestData.VOTE_4.getTitle(),
    TestData.VOTE_4.getContent(),
    TestData.VOTE_4.getCoverImageUrl());

  /**
   * Some random data for update queries in tests
   */
  public static final UpdateVoteInput UPDATE_VOTE_INPUT = new UpdateVoteInput(
    VOTE_4.getId(),
    VOTE_4.getTitle(),
    VOTE_4.getContent(),
    VOTE_4.getCoverImageUrl());

  /**
   * Some data for vote queries in tests
   */
  public static final VoteQuery VOTE_QUERY = new VoteQuery(TestData.SOME_ID);
}
