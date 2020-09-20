package web.jome17.jome_member.bean;

public class ScoreBean {
	private int scoreId = -1;
	private String beRatedId = "";
	private String memberId = "";
	private int score = -1;
	
	public ScoreBean(int scoreId, String beRatedId, String memberId, int score) {
		super();
		this.scoreId = scoreId;
		this.beRatedId = beRatedId;
		this.memberId = memberId;
		this.score = score;
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

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
}
