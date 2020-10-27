package web.jome17.jome_notify.bean;

public class AttenderBean {
private int attenderNo, attendStatus, role;
private String groupName, memberId, modifyDate, memberName, groupId;


public AttenderBean() {
	super();
}



public AttenderBean(int attenderNo, String groupId, int attendStatus, int role, String groupName, String memberId,
		String modifyDate, String memberName) {
	super();
	this.attenderNo = attenderNo;
	this.groupId = groupId;
	this.attendStatus = attendStatus;
	this.role = role;
	this.groupName = groupName;
	this.memberId = memberId;
	this.modifyDate = modifyDate;
	this.memberName = memberName;
}



public int getAttenderNo() {
	return attenderNo;
}

public void setAttenderNo(int attenderNo) {
	this.attenderNo = attenderNo;
}

public String getGroupId() {
	return groupId;
}

public void setGroupId(String groupId) {
	this.groupId = groupId;
}

public int getAttendStatus() {
	return attendStatus;
}

public void setAttendStatus(int attendStatus) {
	this.attendStatus = attendStatus;
}

public int getRole() {
	return role;
}

public void setRole(int role) {
	this.role = role;
}

public String getMemberId() {
	return memberId;
}

public void setMemberId(String memberId) {
	this.memberId = memberId;
}

public String getModifyDate() {
	return modifyDate;
}

public void setModifyDate(String modifyDate) {
	this.modifyDate = modifyDate;
}

public String getGroupName() {
	return groupName;
}

public void setGroupName(String groupName) {
	this.groupName = groupName;
}



public String getMemberName() {
	return memberName;
}



public void setMemberName(String memberName) {
	this.memberName = memberName;
} 





}
