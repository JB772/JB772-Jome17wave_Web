package web.jome17.jome_member.service;

import java.util.List;
import web.jome17.jome_member.bean.MemberBean;
import web.jome17.jome_member.dao.CommonDao;
import web.jome17.jome_member.dao.MemberDaoimpl;

public class JomeMemberService {
	private CommonDao<MemberBean, String> dao;
	private MemberBean member;
	
	public JomeMemberService() {
		dao = new MemberDaoimpl();
	}
	
	
	//註冊，拿到Member物件，檢查account是否存在，存在return，不存在就insert
	public int register(MemberBean member) {
		int acoountExist = 2;
		//int registerSuccess = 1;
		String registrAccount = member.getAccount();
		if (dao.selectByKey("ACCOUNT", registrAccount) == null) {
			return dao.insert(member);
		}else {
			return acoountExist;
		}
	}
	//登入
	//拿到acconut及password，用account去selecByKey，若回傳的Member物件==null，return ；
	//若回傳的Member物件!=null，再檢查password是否 ==。
	public MemberBean login(String account, String password) {
		System.out.println("account:"+account);
		System.out.println("password:"+password);
		member = dao.login(account, password);
		if (member == null) {
			return null;
		}else {
			return member;
		}
	}
	//修改會員資料
	public int updateMember(MemberBean member) {
		int updateFalse = -1;
		if (dao.selectByKey("ACCOUNT", member.getAccount()) == null) {
			return updateFalse;
		}else {
			return dao.update(member);
		}
	}
	
	//查詢單一帳號
	public MemberBean selectMemberOne(String account) {
		MemberBean selecMember = null;
		selecMember = dao.selectByKey("ACCOUNT", account);
		return selecMember;
	}
	
	//拿圖片
	public byte[] getImage(String account) {
		byte[] image = null;
		image = dao.getImage(account);
		return image;
	}
	
	
	//查詢附近用戶
	public List<MemberBean> searchNearBy(String memberId){
		List<MemberBean> members = null;
		members = dao.selectAll(memberId);
		return members;
	}
	
	
	
	/*
	 * 前台用不到的方法
	 */
	//刪除會員資料
	public int deletaMember(String account) {
		return dao.deletaByKey(account);
	}

	

}
