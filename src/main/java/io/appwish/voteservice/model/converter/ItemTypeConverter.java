package io.appwish.voteservice.model.converter;

import io.appwish.grpc.ItemTypeProto;
import io.appwish.voteservice.model.type.ItemType;
import net.badata.protobuf.converter.type.TypeConverter;

/**
 * Allows converting {@link ItemType} objects to {@link ItemTypeProto} and reverse.
 */
public class ItemTypeConverter implements TypeConverter<ItemType, ItemTypeProto> {

  @Override
  public ItemType toDomainValue(final Object instance) {
    if (instance == ItemTypeProto.WISH) {
      return ItemType.WISH;
    }

    if (instance == ItemTypeProto.COMMENT) {
      return ItemType.COMMENT;
    }

    throw new IllegalArgumentException("Instance is none of valid values of ItemTypeProto");
  }

  @Override
  public ItemTypeProto toProtobufValue(final Object instance) {
    if (instance == ItemType.WISH) {
      return ItemTypeProto.WISH;
    }

    if (instance == ItemType.COMMENT) {
      return ItemTypeProto.COMMENT;
    }

    throw new IllegalArgumentException("Instance is none of valid values of ItemType");
  }
}
