package io.appwish.voteservice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import oi.appwish.voteservice.model.ItemType;
import oi.appwish.voteservice.model.Vote;
import oi.appwish.voteservice.model.VoteType;
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
  public static final long SOME_USER_ID = 9999;
  public static final long SOME_ITEM_ID = 8888;
  public static final ItemType SOME_ITEM_TYPE = ItemType.WISH;
  public static final LocalDateTime SOME_CREATED_AT = LocalDateTime.now();
  public static final VoteType SOME_VOTE_TYPE = VoteType.DOWN;

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
  public static final Vote VOTE_1 = new Vote(SOME_ID, SOME_USER_ID, SOME_ITEM_ID, SOME_ITEM_TYPE, SOME_CREATED_AT, SOME_VOTE_TYPE);
  public static final Vote VOTE_2 = new Vote(2, 2020, 2000, ItemType.WISH, LocalDateTime.now(), VoteType.UP);
  public static final Vote VOTE_3 = new Vote(3, 3030, 3000, ItemType.WISH, LocalDateTime.now(), VoteType.UP);
  public static final Vote VOTE_4 = new Vote(4, 4040, 4000, ItemType.COMMENT, LocalDateTime.now(), VoteType.DOWN);

  /**
   * List of random votes to be used in tests
   */
  public static final List<Vote> VOTES = Arrays.asList(VOTE_1, VOTE_2, VOTE_3, VOTE_4);

  /**
   * All vote query to be used in tests
   */
  public static final AllVoteQuery ALL_VOTE_QUERY = new AllVoteQuery();

  /**
   * Some random inputs to be used in tests
   */
  public static final VoteInput VOTE_INPUT_1 = new VoteInput(
    TestData.VOTE_1.getUserId(),
    TestData.VOTE_1.getItemId(),
    TestData.VOTE_1.getItemType(),
    TestData.VOTE_1.getCreatedAt(),
    TestData.VOTE_1.getVoteType());
  public static final VoteInput VOTE_INPUT_2 = new VoteInput(
    TestData.VOTE_2.getUserId(),
    TestData.VOTE_2.getItemId(),
    TestData.VOTE_2.getItemType(),
    TestData.VOTE_2.getCreatedAt(),
    TestData.VOTE_2.getVoteType());
  public static final VoteInput VOTE_INPUT_3 = new VoteInput(
    TestData.VOTE_3.getUserId(),
    TestData.VOTE_3.getItemId(),
    TestData.VOTE_3.getItemType(),
    TestData.VOTE_3.getCreatedAt(),
    TestData.VOTE_3.getVoteType());
  public static final VoteInput VOTE_INPUT_4 = new VoteInput(
    TestData.VOTE_4.getUserId(),
    TestData.VOTE_4.getItemId(),
    TestData.VOTE_4.getItemType(),
    TestData.VOTE_4.getCreatedAt(),
    TestData.VOTE_4.getVoteType());

  /**
   * Some random data for update queries in tests
   */
  public static final UpdateVoteInput UPDATE_VOTE_INPUT = new UpdateVoteInput(
	TestData.VOTE_4.getUserId(),		  
    TestData.VOTE_4.getUserId(),
    TestData.VOTE_4.getItemId(),
    TestData.VOTE_4.getItemType(),
    TestData.VOTE_4.getCreatedAt(),
    TestData.VOTE_4.getVoteType());

  /**
   * Some data for vote queries in tests
   */
  public static final VoteQuery VOTE_QUERY = new VoteQuery(TestData.SOME_ID);
}
