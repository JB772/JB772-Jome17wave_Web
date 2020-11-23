package web.jome17.jome_member.service;

import java.util.List;

import web.jome17.jome_member.bean.FriendListBean;
import web.jome17.jome_member.bean.MemberBean;
import web.jome17.jome_member.bean.ScoreBean;
import web.jome17.jome_member.dao.CommonDao;
import web.jome17.jome_member.dao.FriendListDaoimpl;
import web.jome17.jome_member.dao.MemberDaoimpl;
import web.jome17.jome_member.dao.ScoreDaoimpl;
import web.jome17.main.DateUtil;

public class JomeMemberService {
	private CommonDao<MemberBean, String> dao;	
	public JomeMemberService() {
		dao = new MemberDaoimpl();
	}
	/*
	 * 註冊
	 * 拿到Member物件，檢查account是否重複，存在return，不存在就insert
	 */
	public int register(MemberBean member) {
		List<MemberBean> allMembers = dao.selectAllNoKey();
		for(MemberBean aMember: allMembers){
			if(aMember.getAccount().equals(member.getAccount())) {
				return -1;
			}
		}
		member.setMemberId(new DateUtil().getDateTimeId());
		return dao.insert(member);
	}
	
	/**
	 * 登入
	 * 拿到acconut及password，用account去selecByKey，若回傳的Member物件==null，return ；
	 * 若回傳的Member物件!=null，再檢查password是否 ==。
	 */
	public MemberBean login(String account, String password) {
		MemberBean memberLogin = null;
		memberLogin = dao.selectByKey("ACCOUNT", account);
		if(memberLogin != null) {
			if(password.equals(memberLogin.getPassword())) {
System.out.println("JomeMemberService accountStatus 44: " + memberLogin.getAccountStatus());
				return memberLogin;
			}
		}
		return null;
	}
	 
	
	//修改會員資料
	public int updateMember(MemberBean member, byte[] image) {
		member.setImage(image);
		return dao.update(member);
	}
	
	//account查詢單一Member
	public MemberBean selectMemberOne(String account) {
		MemberBean selecMember = null;
		selecMember = dao.selectByKey("ACCOUNT", account);
		return selecMember;
	}
	
	//memberID查詢單一Member
	public MemberBean selectMemberById(String memberId) {
		MemberBean idMember = null;
		idMember = dao.selectByKey("ID", memberId);
		return idMember;
	}
	
	//memberID查詢registrationToken
	public String selectTokenById(String memberId) {
		 MemberBean idMember = new MemberBean();
		idMember = dao.selectByKey("ID", memberId);
		 String tokenId = idMember.getTokenId();
		 if(tokenId == null || tokenId.equals("noTokenId")) {
			 return "noTokenId";
		 }
		return tokenId;
	}
	
	//拿圖片
	public byte[] getImage(String account) {
		byte[] image = null;
		image = dao.getImage(account);
		return image;
	}
	
	
	//查詢好朋友數量
	public String getFriendCount(String memberId) {
		CommonDao<FriendListBean, String> friendListDaoimpl = new FriendListDaoimpl();
		String friendCount = friendListDaoimpl.getCount(memberId);;
		if(friendCount.isEmpty()) {
			friendCount = "";
		}
		return friendCount;
	}
	
	//查詢各評分次數
	public String getScoreCounts(String memberId) {
		CommonDao<ScoreBean, String> scoreDaoimpl = new ScoreDaoimpl();
		String scoreCounts = scoreDaoimpl.getCount(memberId);
		if(scoreCounts == null) {
			scoreCounts = "";
		}
		return scoreCounts;
		
	}
	
	//查詢附近用戶
	public List<MemberBean> searchNearBy(String memberId){
		List<MemberBean> members = null;
		members = dao.selectAll(memberId);
		return members;
	}
	
	public List<MemberBean> selectAllMember(){
		List<MemberBean> members = null;
		members = dao.selectAllNoKey();
		return members;
	}
	
	
	
	/*
	 * 前台用不到的方法
	 */
	//刪除會員資料
	public int deletaMember(String account, String key) {
		return dao.deletaByKey(account, key);
	}

	//取得所有會員
	public List<MemberBean> getAllMember() {
		return new MemberDaoimpl().getAll();
	}

}
