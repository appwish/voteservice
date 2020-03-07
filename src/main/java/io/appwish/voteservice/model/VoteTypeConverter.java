package io.appwish.voteservice.model;

import io.appwish.grpc.VoteTypeProto;
import net.badata.protobuf.converter.type.TypeConverter;

public class VoteTypeConverter implements TypeConverter<VoteType, VoteTypeProto> {

  @Override
  public VoteType toDomainValue(final Object instance) {
    if (instance == VoteTypeProto.DOWN) {
      return VoteType.DOWN;
    }

    if (instance == VoteTypeProto.UP) {
      return VoteType.UP;
    }

    throw new IllegalArgumentException("Instance is none of valid values of VoteTypeProto");
  }

  @Override
  public VoteTypeProto toProtobufValue(final Object instance) {
    if (instance == VoteType.DOWN) {
      return VoteTypeProto.DOWN;
    }

    if (instance == VoteType.UP) {
      return VoteTypeProto.UP;
    }

    throw new IllegalArgumentException("Instance is none of valid values of VoteType");
  }

}
