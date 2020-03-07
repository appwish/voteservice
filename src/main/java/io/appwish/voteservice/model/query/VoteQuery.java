package io.appwish.voteservice.model.query;

import io.appwish.grpc.VoteQueryProto;
import java.util.Objects;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

/**
 * This type should be used to query single vote from the database.
 *
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(VoteQueryProto.class)
public class VoteQuery {

  @ProtoField
  private long id;

  public VoteQuery(final long id) {
    this.id = id;
  }

  public VoteQuery() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VoteQuery voteQuery = (VoteQuery) o;
    return id == voteQuery.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "VoteQuery{" +
      "id=" + id +
      '}';
  }
}
