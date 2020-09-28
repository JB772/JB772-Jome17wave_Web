package web.jome17.jome_member.bean;

import java.util.Date;

public class MemberBean {
	private String member_id;
	private String build_date;
	private String modify_date;
	private int account_status;
	private String phone_number;
	private String nickname;
	private String account;
	private String password;
	private int gender;
	private double latitude;
	private double longitude;
	private String token_id;
	private byte[] image;
    private Integer friendCount = 0;
    private Double scoreAverage = 0.0;
    private Integer groupCount = 0;
	
	public MemberBean(String phone_number, String nickname, String account, String password, int gender,
			double latitude, double longitude, String token_id) {
		super();
		this.phone_number = phone_number;
		this.nickname = nickname;
		this.account = account;
		this.password = password;
		this.gender = gender;
		this.latitude = latitude;
		this.longitude = longitude;
		this.token_id = token_id;
	}

	public MemberBean(String account, String password) {
		super();
		this.account = account;
		this.password = password;
	}
	
	

	public MemberBean() {
//		super();
	}



	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	

	public String getBuild_date() {
		return build_date;
	}

	public void setBuild_date(String build_date) {
		this.build_date = build_date;
	}

	public String getModify_date() {
		return modify_date;
	}

	public void setModify_date(String modify_date) {
		this.modify_date = modify_date;
	}

	public int getAccount_status() {
		return account_status;
	}

	public void setAccount_status(int account_status) {
		this.account_status = account_status;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getToken_id() {
		return token_id;
	}

	public void setToken_id(String token_id) {
		this.token_id = token_id;
	}

	public Integer getFriendCount() {
		return friendCount;
	}

	public void setFriendCount(Integer friendCount) {
		this.friendCount = friendCount;
	}

	public Double getScoreAverage() {
		return scoreAverage;
	}

	public void setScoreAverage(Double scoreAverage) {
		this.scoreAverage = scoreAverage;
	}

	public Integer getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(Integer groupCount) {
		this.groupCount = groupCount;
	}
	
	
	
	
	
	
	
}
