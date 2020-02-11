package oi.appwish.voteservice.model.input;

import java.time.LocalDateTime;

import io.appwish.grpc.UpdateVoteInputProto;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;
import oi.appwish.voteservice.model.ItemType;
import oi.appwish.voteservice.model.VoteType;

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
	private long id;

	@ProtoField
	private long userId;

	@ProtoField
	private long itemId;

	@ProtoField
	private ItemType itemType;

	@ProtoField
	private LocalDateTime createdAt;

	@ProtoField
	private VoteType voteType;

	public UpdateVoteInput() {
		super();
	}

	/**
	 * @param id
	 * @param userId
	 * @param itemId
	 * @param itemType
	 * @param createdAt
	 * @param voteType
	 */
	public UpdateVoteInput(long id, long userId, long itemId, ItemType itemType, LocalDateTime createdAt, VoteType voteType) {
		super();
		this.id = id;
		this.userId = userId;
		this.itemId = itemId;
		this.itemType = itemType;
		this.createdAt = createdAt;
		this.voteType = voteType;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
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
	 * @return the createdAt
	 */
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
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
		result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
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
		if (createdAt == null) {
			if (other.createdAt != null)
				return false;
		} else if (!createdAt.equals(other.createdAt))
			return false;
		if (id != other.id)
			return false;
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
		builder.append(", createdAt=");
		builder.append(createdAt);
		builder.append(", voteType=");
		builder.append(voteType);
		builder.append("]");
		return builder.toString();
	}

}
