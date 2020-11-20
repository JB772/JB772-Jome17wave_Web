package web.jome17.jome_member.bean;

public class AdminBean {
	
	private int adminId = -1;
	private String adminAccount = "noAccount";
	private String adminPassword = "noPassword";
	private int loginCount = -1;
	private String lastLoginTime = "noLoginTime";
	private byte[] image = null;
	
	public AdminBean(int adminId, String adminAccount, String adminPassword, int loginCount, String lastLoginTime) {
		super();
		this.adminId = adminId;
		this.adminAccount = adminAccount;
		this.adminPassword = adminPassword;
		this.loginCount = loginCount;
		this.lastLoginTime = lastLoginTime;
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}

	public String getAdminAccount() {
		return adminAccount;
	}

	public void setAdminAccount(String adminAccount) {
		this.adminAccount = adminAccount;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public int getLoginCount() {
		return loginCount;
	}

	public void setLoginCount(int loginCount) {
		this.loginCount = loginCount;
	}

	public String getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}
	
	
}
