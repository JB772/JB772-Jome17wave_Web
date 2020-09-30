package web.jome17.jome_notify.bean;

import java.io.Serializable;
import java.util.Date;

public class Notify {
	private int notificationId, type, notificationBody;
	private String memberId;
	private Date buildDate;
	
	public Notify() {
		super();
	}

	public Notify(int notificationId, int type, int notificationBody, String memberId) {
		super();
		this.notificationId = notificationId;
		this.type = type;
		this.notificationBody = notificationBody;
		this.memberId = memberId;
	}

	public int getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getNotificationBody() {
		return notificationBody;
	}

	public void setNotificationBody(int notificationBody) {
		this.notificationBody = notificationBody;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public Date getBuildDate() {
		return buildDate;
	}

	public void setBuildDate(Date buildDate) {
		this.buildDate = buildDate;
	}


	
	
}
