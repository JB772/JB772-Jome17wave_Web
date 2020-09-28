package web.jome17.jome_member.bean;

import java.util.Date;

public class ScoreBean {
	private int scoreId = -1;
	private int groupId = -1;
	private String beRatedId = "";
	private String memberId = "";
	private String memberNickname = "";
	private int ratingScore = -1;
	private Date modifyDate = new Date();
	
	

	public ScoreBean(int scoreId, int groupId, String beRatedId, String memberId, int ratingScore) {
		super();
		this.scoreId = scoreId;
		this.groupId = groupId;
		this.beRatedId = beRatedId;
		this.memberId = memberId;
		this.ratingScore = ratingScore;
	}

	public ScoreBean() {
		super();
	}

	public int getScoreId() {
		return scoreId;
	}

	public void setScoreId(int scoreId) {
		this.scoreId = scoreId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
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

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getMemberNickname() {
		return memberNickname;
	}

	public void setMemberNickname(String memberNickname) {
		this.memberNickname = memberNickname;
	}

	
	
}
