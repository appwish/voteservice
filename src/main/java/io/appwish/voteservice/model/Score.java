package io.appwish.voteservice.model;

import io.appwish.grpc.VoteScoreProto;
import java.util.Objects;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

/**
 * This type should be used to represent item's vote score.
 * <p>
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link net.badata.protobuf.converter.Converter} to convert back/forth between
 * protobuf data transfer objects and model objects.
 * <p>
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(VoteScoreProto.class)
public class Score {

  @ProtoField
  private long score;

  @ProtoField
  private long up;

  @ProtoField
  private long down;

  public Score(long up, long down) {
    this.score = up - down;
    this.up = up;
    this.down = down;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Score score1 = (Score) o;
    return score == score1.score &&
        up == score1.up &&
        down == score1.down;
  }

  @Override
  public int hashCode() {
    return Objects.hash(score, up, down);
  }

  @Override
  public String toString() {
    return "Score{" +
        "score=" + score +
        ", up=" + up +
        ", down=" + down +
        '}';
  }

  public long getScore() {
    return score;
  }

  public void setScore(long score) {
    this.score = score;
  }

  public long getUp() {
    return up;
  }

  public void setUp(long up) {
    this.up = up;
  }

  public long getDown() {
    return down;
  }

  public void setDown(long down) {
    this.down = down;
  }
}
