package web.jome17.jome_member.service;

import web.jome17.jome_member.bean.AdminBean;
import web.jome17.jome_member.dao.AdminDaoimpl;
import web.jome17.jome_member.dao.CommonDao;

public class AdminService {
	private CommonDao<AdminBean, String> dao;
	public AdminService() {
		dao = new AdminDaoimpl();
	}

	//管理員登入
	public int login(String account, String password) {
		AdminBean adminLogin = null;
		int loginResult = -1;
		adminLogin = dao.selectByKey("adminAccount", account);
		if(adminLogin != null) {
			if (password.equals(adminLogin.getAdminPassword())) {
				adminLogin.setLoginCount(adminLogin.getLoginCount() + 1);
				loginResult = dao.update(adminLogin);
			}
		}
		return loginResult;
	} 
}
