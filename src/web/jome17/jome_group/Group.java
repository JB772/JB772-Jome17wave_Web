package web.jome17.jome_group;

import java.sql.Timestamp;

public class Group {

	private int id;
	private String name;
	private Timestamp assembleTime;
	private Timestamp groupEndTime;
	private Timestamp signUpEnd;
	private Timestamp modifyTime;
	private int surfPointId;
	private int groupLimit;
	private int gender;
	private String notice;
	private int groupStatus;
	
	
	public Group(int id, String name, Timestamp assembleTime, Timestamp groupEndTime, Timestamp signUpEnd,
			Timestamp modifyTime, int surfPointId, int groupLimit, int gender, String notice, int groupStatus) {
		super();
		this.id = id;
		this.name = name;
		this.assembleTime = assembleTime;
		this.groupEndTime = groupEndTime;
		this.signUpEnd = signUpEnd;
		this.modifyTime = modifyTime;
		this.surfPointId = surfPointId;
		this.groupLimit = groupLimit;
		this.gender = gender;
		this.notice = notice;
		this.groupStatus = groupStatus;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Timestamp getAssembleTime() {
		return assembleTime;
	}


	public void setAssembleTime(Timestamp assembleTime) {
		this.assembleTime = assembleTime;
	}


	public Timestamp getGroupEndTime() {
		return groupEndTime;
	}


	public void setGroupEndTime(Timestamp groupEndTime) {
		this.groupEndTime = groupEndTime;
	}


	public Timestamp getSignUpEnd() {
		return signUpEnd;
	}


	public void setSignUpEnd(Timestamp signUpEnd) {
		this.signUpEnd = signUpEnd;
	}


	public Timestamp getModifyTime() {
		return modifyTime;
	}


	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
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


	public String getNotice() {
		return notice;
	}


	public void setNotice(String notice) {
		this.notice = notice;
	}


	public int getGroupStatus() {
		return groupStatus;
	}


	public void setGroupStatus(int groupStatus) {
		this.groupStatus = groupStatus;
	}
	
	
}
