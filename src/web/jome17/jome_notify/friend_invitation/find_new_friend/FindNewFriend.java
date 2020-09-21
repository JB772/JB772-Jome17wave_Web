package web.jome17.jome_notify.friend_invitation.find_new_friend;

import java.util.Date;

public class FindNewFriend {
	 	private int uId, friendStatus;
	    private String inviteMId, acceptMId, acceptMName, acceptAccount;
	    private Date motifyDate;
	    
	    
		public FindNewFriend() {
			super();
		}






		public FindNewFriend(int uId, int friendStatus, String inviteMId, String acceptMId) {
			super();
			this.uId = uId;
			this.friendStatus = friendStatus;
			this.inviteMId = inviteMId;
			this.acceptMId = acceptMId;
		}






		public FindNewFriend(String acceptMId, String acceptMName, String acceptAccount) {
			super();
			this.acceptMId = acceptMId;
			this.acceptMName = acceptMName;
			this.acceptAccount = acceptAccount;
		}






		public int getuId() {
			return uId;
		}

		public void setuId(int uId) {
			this.uId = uId;
		}



		public String getInviteMId() {
			return inviteMId;
		}



		public void setInviteMId(String inviteMId) {
			this.inviteMId = inviteMId;
		}



		public String getAcceptMId() {
			return acceptMId;
		}



		public void setAcceptMId(String acceptMId) {
			this.acceptMId = acceptMId;
		}



		public int getFriendStatus() {
			return friendStatus;
		}

		public void setFriendStatus(int friendStatus) {
			this.friendStatus = friendStatus;
		}

		public String getAcceptMName() {
			return acceptMName;
		}

		public void setAcceptMName(String acceptMName) {
			this.acceptMName = acceptMName;
		}

		public String getAcceptAccount() {
			return acceptAccount;
		}

		public void setAcceptAccount(String acceptAccount) {
			this.acceptAccount = acceptAccount;
		}

		public Date getMotifyDate() {
			return motifyDate;
		}

		public void setMotifyDate(Date motifyDate) {
			this.motifyDate = motifyDate;
		}
	    
		
	    
}
