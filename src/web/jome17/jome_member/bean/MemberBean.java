package web.jome17.jome_member.bean;


public class MemberBean {
	private String member_id = "noMemberId";
	private int account_status = -1;
	private String phone_number = "noPhoneNumber";
	private String nickname = "noNickname";
	private String account = "noAccount";
	private String password = "noPassword";
	private int gender = -1;
	private double latitude = -1.0;
	private double longitude = -1.0;
	private String token_id = "noTokenId";
	private byte[] image = null;
    private String friendCount = "0";
    private String scoreAverage = "0";
    private String beRankedCount = "0";
    private String groupCount = "0";
    private String createGroupCount = "0";
	
    

	public MemberBean(String phone_number, String nickname, String account, String password, int gender,
			String token_id) {
		super();
		this.phone_number = phone_number;
		this.nickname = nickname;
		this.account = account;
		this.password = password;
		this.gender = gender;
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

	public String getFriendCount() {
		return friendCount;
	}

	public void setFriendCount(String friendCount) {
		this.friendCount = friendCount;
	}

	public String getScoreAverage() {
		return scoreAverage;
	}

	public void setScoreAverage(String scoreAverage) {
		this.scoreAverage = scoreAverage;
	}

	public String getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(String groupCount) {
		this.groupCount = groupCount;
	}

	public String getCreateGroupCount() {
		return createGroupCount;
	}

	public void setCreateGroupCount(String createGroupCount) {
		this.createGroupCount = createGroupCount;
	}



	public String getBeRankedCount() {
		return beRankedCount;
	}


	public void setBeRankedCount(String beRankedCount) {
		this.beRankedCount = beRankedCount;
	}
	
	
}
