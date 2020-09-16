package web.jome17.jome_member.service;

import java.util.List;

import web.jome17.jome_member.bean.JomeMember;
import web.jome17.jome_member.dao.JomeMemberDao;
import web.jome17.jome_member.dao.MemberDaoimpl;

public class JomeMemberService {
	private JomeMemberDao<JomeMember, String>dao;

	public JomeMemberService() {
		dao = new MemberDaoimpl();
	}
	
	//註冊，拿到Member物件，檢查account是否存在，存在return，不存在就insert
	public int register(JomeMember member) {
		int acoountExist = 2;
		//int registerSuccess = 1;
		String registrAccount = member.getAccount();
		if (dao.selectByKey(registrAccount) == null) {
			return dao.insert(member);
		}else {
			return acoountExist;
		}
	}
	//登入
	//拿到acconut及password，用account去selecByKey，若回傳的Member物件==null，return ；
	//若回傳的Member物件!=null，再檢查password是否 ==。
	public int login(String account, String password) {
		int loginSuccess = 1;
		int loginFalse = -1;
		System.out.println("account:"+account);
		System.out.println("password:"+password);
		
		if (dao.selectByKey(account) == null) {
			return loginFalse;
		}else {
			return loginSuccess;
		}
	}
	//修改會員資料
	public int updateMember(JomeMember member) {
		int updateFalse = -1;
		if (dao.selectByKey(member.getAccount()) == null) {
			return updateFalse;
		}else {
			return dao.update(member);
		}
	}
	//刪除會員資料
	public int deletaMember(String account) {
		return dao.deletaByKey(account);
	}
	//查詢MEMBER資料表
	public List<JomeMember> selectMemberAll() {
		return dao.selectAll();
	}
	
	//查詢單一帳號
	public JomeMember selectMemberOne(String account) {
		return dao.selectByKey(account);
	}
	
	//拿圖片
	public byte[] getImage(String account) {
		return dao.getImage(account);
	}
}
