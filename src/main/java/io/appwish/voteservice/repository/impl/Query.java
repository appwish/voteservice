package io.appwish.voteservice.repository.impl;

/**
 * Contains queries to execute on Postgres.
 *
 * I'm not sure what's the best practice for storing String SQLs, so for now it'll stay here.
 */
public enum Query {
  FIND_ALL_VOTE("SELECT * FROM votes"),
  FIND_ONE_VOTE("SELECT * FROM votes WHERE id=$1"),
  DELETE_VOTE_QUERY("DELETE FROM votes WHERE id=$1"),
  INSERT_VOTE_QUERY(
    "INSERT INTO votes ("
      + "user_id, "
      + "item_id, "
      + "item_type, "
      + "vote_type, "
      + "created_at) "
      + "VALUES ($1, $2, $3, $4, $5) "
      + "RETURNING *"),
  UPDATE_VOTE_QUERY(
    "UPDATE votes SET "
      + "vote_type=$2 "
      + "WHERE item_id=$1 RETURNING *"),
  CREATE_VOTE_TABLE(
    "CREATE TABLE IF NOT EXISTS votes("
      + "id serial PRIMARY KEY, "
        + "user_id serial, "
        + "item_id serial, "
        + "item_type varchar(255), "
        + "vote_type varchar(255), "
        + "created_at timestamp);");


  private final String sql;

  Query(final String sql) {
    this.sql = sql;
  }

  public String sql() {
    return sql;
  }
}
