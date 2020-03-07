package io.appwish.voteservice.model.input;

import io.appwish.voteservice.model.ItemTypeConverter;
import io.appwish.voteservice.model.VoteTypeConverter;

import io.appwish.grpc.UpdateVoteInputProto;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;
import io.appwish.voteservice.model.ItemType;
import io.appwish.voteservice.model.VoteType;

/**
 * This type should be used as input for updates of voteTypes in the database.
 *
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(UpdateVoteInputProto.class)
public class UpdateVoteInput {

	@ProtoField
	private long userId;

	@ProtoField
	private long itemId;

	@ProtoField(converter = ItemTypeConverter.class)
	private ItemType itemType;

	@ProtoField(converter = VoteTypeConverter.class)
	private VoteType voteType;

	public UpdateVoteInput() {
		super();
	}

	/**
	 * @param userId
	 * @param itemId
	 * @param itemType
	 * @param voteType
	 */
	public UpdateVoteInput(long userId, long itemId, ItemType itemType, VoteType voteType) {
		super();
		this.userId = userId;
		this.itemId = itemId;
		this.itemType = itemType;
		this.voteType = voteType;
	}

	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * @return the itemId
	 */
	public long getItemId() {
		return itemId;
	}

	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return the itemType
	 */
	public ItemType getItemType() {
		return itemType;
	}

	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	/**
	 * @return the voteType
	 */
	public VoteType getVoteType() {
		return voteType;
	}

	/**
	 * @param voteType the voteType to set
	 */
	public void setVoteType(VoteType voteType) {
		this.voteType = voteType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (itemId ^ (itemId >>> 32));
		result = prime * result + ((itemType == null) ? 0 : itemType.hashCode());
		result = prime * result + (int) (userId ^ (userId >>> 32));
		result = prime * result + ((voteType == null) ? 0 : voteType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UpdateVoteInput other = (UpdateVoteInput) obj;
		if (itemId != other.itemId)
			return false;
		if (itemType != other.itemType)
			return false;
		if (userId != other.userId)
			return false;
		if (voteType != other.voteType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UpdateVoteInput [userId=");
		builder.append(userId);
		builder.append(", itemId=");
		builder.append(itemId);
		builder.append(", itemType=");
		builder.append(itemType);
		builder.append(", voteType=");
		builder.append(voteType);
		builder.append("]");
		return builder.toString();
	}

}
