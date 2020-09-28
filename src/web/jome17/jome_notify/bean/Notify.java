package web.jome17.jome_notify.bean;

import java.io.Serializable;
import java.util.Date;

public class Notify {
	private int notificationId, type;
	private String notificationBody;
	private Date buildDate;
	
	public Notify() {
		super();
	}


	public Notify(int notificationId, int type, String notificationBody) {
		super();
		this.notificationId = notificationId;
		this.type = type;
		this.notificationBody = notificationBody;
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
	public String getNotificationBody() {
		return notificationBody;
	}
	public void setNotificationBody(String notificationBody) {
		this.notificationBody = notificationBody;
	}
	public Date getBuildDate() {
		return buildDate;
	}
	public void setBuildDate(Date buildDate) {
		this.buildDate = buildDate;
	}
	
	
}
