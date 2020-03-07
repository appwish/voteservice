package io.appwish.voteservice.model.reply;

import java.util.List;
import java.util.Objects;

import io.appwish.grpc.AllVoteReplyProto;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;
import io.appwish.voteservice.model.Vote;

/**
 * Represents data to return for multiple votes query. Right now it contains just a list of votes,
 * but later we'll add pagination etc.
 *
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(AllVoteReplyProto.class)
public class AllVoteReply {

  @ProtoField
  private List<Vote> votes;

  public AllVoteReply(final List<Vote> votes) {
    this.votes = votes;
  }

  public AllVoteReply() {
  }

  public List<Vote> getVotees() {
    return votes;
  }

  public void setVotees(final List<Vote> votes) {
    this.votes = votes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AllVoteReply that = (AllVoteReply) o;
    return votes.equals(that.votes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(votes);
  }

  @Override
  public String toString() {
    return "AllVoteReply{" +
      "votes=" + votes +
      '}';
  }
}
