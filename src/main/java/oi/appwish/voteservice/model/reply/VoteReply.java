package oi.appwish.voteservice.model.reply;

import java.util.Objects;

import io.appwish.grpc.VoteReplyProto;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;
import oi.appwish.voteservice.model.Vote;

/**
 * Represents data to return for single vote query.
 *
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(VoteReplyProto.class)
public class VoteReply {

  @ProtoField
  private Vote vote;

  public VoteReply(final Vote vote) {
    this.vote = vote;
  }

  public VoteReply() {
  }

  public Vote getVote() {
    return vote;
  }

  public void setVote(final Vote vote) {
    this.vote = vote;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VoteReply voteReply = (VoteReply) o;
    return Objects.equals(vote, voteReply.vote);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vote);
  }

  @Override
  public String toString() {
    return "VoteReply{" +
      "vote=" + vote +
      '}';
  }
}
