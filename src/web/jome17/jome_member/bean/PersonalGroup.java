package web.jome17.jome_member.bean;

public class PersonalGroup {
	private String memberId = "";
	private String nickname = "";
	private int memberGender = -1;
	private int attenderId = -1;
	private int attenderStatus = -1;
	private int role = -1;
	private String surfName = "noData";
	private String groupId = "";
	private String groupName = "noGroupName";
	private String assembleTime = "";
	private String groupEndTime = "";
	private String signUpEnd = "";
	private int surfPointId = -500;
	private int groupLimit = -1;
	private int joinCountNow = 1;
	private int gender = -1;
	private int groupStatus = -1;
	private String notice = "";
	private byte[] gImage = null;
	
	public PersonalGroup(String memberId, int attenderId, String groupId) {
		this.memberId = memberId;
		this.attenderId = attenderId;
		this.groupId = groupId;
	}

	
	public PersonalGroup(String memberId, String groupId, String groupName, String assembleTime, String groupEndTime,
			String signUpEnd, int surfPointId, int groupLimit, String notice) {
		super();
		this.memberId = memberId;
		this.groupId = groupId;
		this.groupName = groupName;
		this.assembleTime = assembleTime;
		this.groupEndTime = groupEndTime;
		this.signUpEnd = signUpEnd;
		this.surfPointId = surfPointId;
		this.groupLimit = groupLimit;
		this.notice = notice;
	}



	public PersonalGroup() {
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	
	public int getMemberGender() {
		return memberGender;
	}

	public void setMemberGender(int memberGender) {
		this.memberGender = memberGender;
	}

	public int getAttenderId() {
		return attenderId;
	}

	public void setAttenderId(int attenderId) {
		this.attenderId = attenderId;
	}

	public int getAttenderStatus() {
		return attenderStatus;
	}

	public void setAttenderStatus(int attenderStatus) {
		this.attenderStatus = attenderStatus;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public String getSurfName() {
		return surfName;
	}

	public void setSurfName(String surfName) {
		this.surfName = surfName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getAssembleTime() {
		return assembleTime;
	}

	public void setAssembleTime(String assembleTime) {
		this.assembleTime = assembleTime;
	}

	public String getGroupEndTime() {
		return groupEndTime;
	}

	public void setGroupEndTime(String groupEndTime) {
		this.groupEndTime = groupEndTime;
	}

	public String getSignUpEnd() {
		return signUpEnd;
	}

	public void setSignUpEnd(String signUpEnd) {
		this.signUpEnd = signUpEnd;
	}

	public int getSurfPointId() {
		return surfPointId;
	}

	public void setSurfPointId(int surfPointId) {
		this.surfPointId = surfPointId;
	}

	public int getGroupLimit() {
		return groupLimit;
	}

	public void setGroupLimit(int groupLimit) {
		this.groupLimit = groupLimit;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}
	
	public int getJoinCountNow() {
		return joinCountNow;
	}

	public void setJoinCountNow(int joinCountNow) {
		this.joinCountNow = joinCountNow;
	}

	public int getGroupStatus() {
		return groupStatus;
	}

	public void setGroupStatus(int groupStatus) {
		this.groupStatus = groupStatus;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public byte[] getgImage() {
		return gImage;
	}

	public void setgImage(byte[] gImage) {
		this.gImage = gImage;
	}
	
}
