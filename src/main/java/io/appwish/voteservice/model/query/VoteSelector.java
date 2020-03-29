package io.appwish.voteservice.model.query;

import io.appwish.grpc.VoteQueryProto;
import io.appwish.voteservice.model.converter.ItemTypeConverter;
import io.appwish.voteservice.model.type.ItemType;
import java.util.Objects;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

/**
 * This type should be used to query votes from the database.
 * <p>
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link net.badata.protobuf.converter.Converter} to convert back/forth between
 * protobuf data transfer objects and model objects.
 * <p>
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(VoteQueryProto.class)
public class VoteSelector {

  @ProtoField
  private long itemId;

  @ProtoField(converter = ItemTypeConverter.class)
  private ItemType itemType;

  public VoteSelector(final long itemId, final ItemType itemType) {
    this.itemId = itemId;
    this.itemType = itemType;
  }

  public VoteSelector() {
  }

  public long getItemId() {
    return itemId;
  }

  public void setItemId(long itemId) {
    this.itemId = itemId;
  }

  public ItemType getItemType() {
    return itemType;
  }

  public void setItemType(ItemType itemType) {
    this.itemType = itemType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VoteSelector voteQuery = (VoteSelector) o;
    return itemId == voteQuery.itemId &&
        itemType.equals(voteQuery.itemType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemId, itemType);
  }

  @Override
  public String toString() {
    return "VoteSelector{" +
        "itemId=" + itemId +
        ", itemType='" + itemType + '\'' +
        '}';
  }
}
