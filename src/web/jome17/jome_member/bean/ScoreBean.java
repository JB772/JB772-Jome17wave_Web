package web.jome17.jome_member.bean;

public class ScoreBean {
	private String groupId = "";
	private String beRatedId = "";
	private String memberId = "";
	private int ratingScore = -1;
	
	public ScoreBean(String groupId, String beRatedId, String memberId, int ratingScore) {
		super();
		this.groupId = groupId;
		this.beRatedId = beRatedId;
		this.memberId = memberId;
		this.ratingScore = ratingScore;
	}

	public ScoreBean() {
		super();
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getBeRatedId() {
		return beRatedId;
	}

	public void setBeRatedId(String beRatedId) {
		this.beRatedId = beRatedId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public int getRatingScore() {
		return ratingScore;
	}

	public void setRatingScore(int ratingScore) {
		this.ratingScore = ratingScore;
	}
	
	
}
