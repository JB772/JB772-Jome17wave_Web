package web.jome17.jome_notify.message_center;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable{
	private int messageId, chatRoomId, sendMId, readStatus;
	private String messageDetail;
	private Date sendTime;
	
	
	public Message() {
		super();
	}


	public Message(int messageId, int chatRoomId, int sendMId, int readStatus, String messageDetail, Date sendTime) {
		super();
		this.messageId = messageId;
		this.chatRoomId = chatRoomId;
		this.sendMId = sendMId;
		this.readStatus = readStatus;
		this.messageDetail = messageDetail;
		this.sendTime = sendTime;
	}


	public int getMessageId() {
		return messageId;
	}


	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}


	public int getChatRoomId() {
		return chatRoomId;
	}


	public void setChatRoomId(int chatRoomId) {
		this.chatRoomId = chatRoomId;
	}


	public int getSendMId() {
		return sendMId;
	}


	public void setSendMId(int sendMId) {
		this.sendMId = sendMId;
	}


	public int getReadStatus() {
		return readStatus;
	}


	public void setReadStatus(int readStatus) {
		this.readStatus = readStatus;
	}


	public String getMessageDetail() {
		return messageDetail;
	}


	public void setMessageDetail(String messageDetail) {
		this.messageDetail = messageDetail;
	}


	public Date getSendTime() {
		return sendTime;
	}


	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	
	
}
