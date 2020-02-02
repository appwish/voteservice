package oi.appwish.voteservice.model.input;

import java.util.Date;

import io.appwish.grpc.WishInputProto;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;
import oi.appwish.voteservice.model.ItemType;
import oi.appwish.voteservice.model.VoteType;

/**
 * This type should be used for inserting new wishes to the database.
 *
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(VoteInputProto.class)
public class VoteInput {

	@ProtoField
	private long userId;

	@ProtoField
	private long itemId;

	@ProtoField
	private ItemType itemType;

	@ProtoField
	private Date createdAt;

	@ProtoField
	private VoteType vote;

	public VoteInput() {

	}

	public VoteInput(long userId, long itemId, ItemType itemType, Date createdAt, VoteType vote) {
		super();
		this.userId = userId;
		this.itemId = itemId;
		this.itemType = itemType;
		this.createdAt = createdAt;
		this.vote = vote;
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
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return the vote
	 */
	public VoteType getVote() {
		return vote;
	}

	/**
	 * @param vote the vote to set
	 */
	public void setVote(VoteType vote) {
		this.vote = vote;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + (int) (itemId ^ (itemId >>> 32));
		result = prime * result + ((itemType == null) ? 0 : itemType.hashCode());
		result = prime * result + (int) (userId ^ (userId >>> 32));
		result = prime * result + ((vote == null) ? 0 : vote.hashCode());
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
		VoteInput other = (VoteInput) obj;
		if (createdAt == null) {
			if (other.createdAt != null)
				return false;
		} else if (!createdAt.equals(other.createdAt))
			return false;
		if (itemId != other.itemId)
			return false;
		if (itemType != other.itemType)
			return false;
		if (userId != other.userId)
			return false;
		if (vote != other.vote)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VoteInput [userId=");
		builder.append(userId);
		builder.append(", itemId=");
		builder.append(itemId);
		builder.append(", itemType=");
		builder.append(itemType);
		builder.append(", createdAt=");
		builder.append(createdAt);
		builder.append(", vote=");
		builder.append(vote);
		builder.append("]");
		return builder.toString();
	}
	
	
}
