package io.appwish.voteservice.model.reply;

import java.util.Objects;

import io.appwish.grpc.VoteDeleteReplyProto;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

/**
 * Represents data to return for delete vote query.
 *
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(VoteDeleteReplyProto.class)
public class VoteDeleteReply {

  @ProtoField
  private boolean deleted;

  public VoteDeleteReply(boolean deleted) {
    this.deleted = deleted;
  }

  public VoteDeleteReply() {
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VoteDeleteReply that = (VoteDeleteReply) o;
    return deleted == that.deleted;
  }

  @Override
  public int hashCode() {
    return Objects.hash(deleted);
  }
}
