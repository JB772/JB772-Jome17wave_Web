package web.jome17.jome_notify.message_center;

import java.io.Serializable;

public class ChatRoom implements Serializable{
	private int chatRoomId, friendMId01, friendMid02;

	public ChatRoom() {
		super();
	}

	public ChatRoom(int chatRoomId, int friendMId01, int friendMid02) {
		super();
		this.chatRoomId = chatRoomId;
		this.friendMId01 = friendMId01;
		this.friendMid02 = friendMid02;
	}

	public int getChatRoomId() {
		return chatRoomId;
	}

	public void setChatRoomId(int chatRoomId) {
		this.chatRoomId = chatRoomId;
	}

	public int getFriendMId01() {
		return friendMId01;
	}

	public void setFriendMId01(int friendMId01) {
		this.friendMId01 = friendMId01;
	}

	public int getFriendMid02() {
		return friendMid02;
	}

	public void setFriendMid02(int friendMid02) {
		this.friendMid02 = friendMid02;
	}
	
}
