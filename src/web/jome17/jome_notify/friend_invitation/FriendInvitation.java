package web.jome17.jome_notify.friend_invitation;

import java.util.Date;

public class FriendInvitation {
	private int friendListId, InviteMId, AcceptMId, FreindStatus;
	private Date ModifyDate;
	
	
	public FriendInvitation() {
		super();
	}
	
	
	public FriendInvitation(int friendListId, int inviteMId, int acceptMId, int freindStatus, Date modifyDate) {
		super();
		this.friendListId = friendListId;
		InviteMId = inviteMId;
		AcceptMId = acceptMId;
		FreindStatus = freindStatus;
		ModifyDate = modifyDate;
	}
	
	
	public int getFriendListId() {
		return friendListId;
	}
	public void setFriendListId(int friendListId) {
		this.friendListId = friendListId;
	}
	public int getInviteMId() {
		return InviteMId;
	}
	public void setInviteMId(int inviteMId) {
		InviteMId = inviteMId;
	}
	public int getAcceptMId() {
		return AcceptMId;
	}
	public void setAcceptMId(int acceptMId) {
		AcceptMId = acceptMId;
	}
	public int getFreindStatus() {
		return FreindStatus;
	}
	public void setFreindStatus(int freindStatus) {
		FreindStatus = freindStatus;
	}
	public Date getModifyDate() {
		return ModifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		ModifyDate = modifyDate;
	}

	
	

}
