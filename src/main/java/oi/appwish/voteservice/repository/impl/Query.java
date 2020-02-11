package oi.appwish.voteservice.repository.impl;

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
      + "createdAt) "
      + "VALUES ($1, $2, $3, $4, $5) "
      + "RETURNING *"),
  UPDATE_VOTE_QUERY(
    "UPDATE votes SET "
      + "item_id=$1, "
      + "content=$2, "
      + "cover_image_url=$3 "
      + "WHERE id=$4 RETURNING *"),
  CREATE_VOTE_TABLE(
    "CREATE TABLE IF NOT EXISTS votes("
      + "id serial PRIMARY KEY, "
      + "title VARCHAR (50) NOT NULL, "
      + "content VARCHAR (255) NOT NULL, "
      + "cover_image_url VARCHAR (255), "
      + "author_id serial, "
      + "url VARCHAR (255));");

  private final String sql;

  Query(final String sql) {
    this.sql = sql;
  }

  public String sql() {
    return sql;
  }
}
