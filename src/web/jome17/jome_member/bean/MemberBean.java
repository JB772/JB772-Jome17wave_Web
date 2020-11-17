package web.jome17.jome_member.bean;


public class MemberBean {
	private String memberId = "noMemberId";
	private int accountStatus = -1;
	private String phoneNumber = "noPhoneNumber";
	private String nickname = "noNickname";
	private String account = "noAccount";			//5
	private String password = "noPassword";
	private int gender = -1;
	private double latitude = -1.0;
	private double longitude = -1.0;
	private String tokenId = "noTokenId";			//10
	private byte[] image = null;
    private String friendCount = "0";
    private String scoreAverage = "0";
    private String beRankedCount = "0";
    private String groupCount = "0";
    private String createGroupCount = "0";			//16
	
    

	public MemberBean(String phone_number, String nickname, String account, String password, int gender,
			String token_id) {
		super();
		this.phoneNumber = phone_number;
		this.nickname = nickname;
		this.account = account;
		this.password = password;
		this.gender = gender;
		this.tokenId = token_id;
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

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String member_id) {
		this.memberId = member_id;
	}

	public int getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(int account_status) {
		this.accountStatus = account_status;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phone_number) {
		this.phoneNumber = phone_number;
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

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String token_id) {
		this.tokenId = token_id;
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
