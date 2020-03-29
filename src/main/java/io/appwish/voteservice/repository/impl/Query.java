package io.appwish.voteservice.repository.impl;

/**
 * Contains queries to execute on Postgres.
 * <p>
 * I'm not sure what's the best practice for storing String SQLs, so for now it'll stay here.
 */
public enum Query {
  VOTE(
      "INSERT INTO votes ("
          + "user_id, "
          + "item_id, "
          + "item_type, "
          + "vote_type, "
          + "created_at) "
          + "VALUES ($1, $2, $3, $4, $5) "
          + "RETURNING *"),
  UNVOTE("DELETE FROM votes WHERE item_id=$1 AND item_type=$2 AND user_id=$3"),
  UPDATE_VOTE(
      "UPDATE votes SET "
          + "vote_type=$1 "
          + "WHERE item_id=$2 AND item_type=$3 AND user_id=$4 RETURNING *"),
  CREATE_VOTE_TABLE(
      "CREATE TABLE IF NOT EXISTS votes("
          + "id serial PRIMARY KEY, "
          + "user_id varchar(50), "
          + "item_id serial, "
          + "item_type varchar(255), "
          + "vote_type varchar(255), "
          + "created_at timestamp);"),
  HAS_VOTED("SELECT vote_type FROM votes WHERE user_id=$1 AND item_id=$2 AND item_type=$3"),
  GET_SCORE("SELECT vote_type, count(*) AS score FROM votes WHERE item_id=$1 AND item_type=$2 GROUP BY vote_type");

  private final String sql;

  Query(final String sql) {
    this.sql = sql;
  }

  public String sql() {
    return sql;
  }
}
