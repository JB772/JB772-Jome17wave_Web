package web.jome17.jome_member.bean;

import java.util.Date;

public class FriendListBean {
    private int uId = 0;
    private String invite_M_ID = "";
    private String accept_M_ID = "";
    private int friend_Status = -1;
    private Date modify_Date = new Date();
    
	public FriendListBean(String invite_M_ID, String accept_M_ID, int friend_Status) {
		super();
		this.invite_M_ID = invite_M_ID;
		this.accept_M_ID = accept_M_ID;
		this.friend_Status = friend_Status;
	}
	
	

	public FriendListBean() {
		super();
	}



	public int getuId() {
		return uId;
	}

	public void setuId(int uId) {
		this.uId = uId;
	}

	public String getInvite_M_ID() {
		return invite_M_ID;
	}

	public void setInvite_M_ID(String invite_M_ID) {
		this.invite_M_ID = invite_M_ID;
	}

	public String getAccept_M_ID() {
		return accept_M_ID;
	}

	public void setAccept_M_ID(String accept_M_ID) {
		this.accept_M_ID = accept_M_ID;
	}

	public int getFriend_Status() {
		return friend_Status;
	}

	public void setFriend_Status(int friend_Status) {
		this.friend_Status = friend_Status;
	}

	public Date getModify_Date() {
		return modify_Date;
	}

	public void setModify_Date(Date modify_Date) {
		this.modify_Date = modify_Date;
	}
    
}
