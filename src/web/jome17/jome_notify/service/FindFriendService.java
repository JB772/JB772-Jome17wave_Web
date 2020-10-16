package web.jome17.jome_notify.service;

import web.jome17.jome_member.bean.FriendListBean;
import web.jome17.jome_member.bean.MemberBean;
import web.jome17.jome_member.dao.CommonDao;
import web.jome17.jome_member.dao.FriendListDaoimpl;
import web.jome17.jome_member.dao.MemberDaoimpl;

public class FindFriendService {
	private CommonDao<FriendListBean, String> dao;

	public FindFriendService() {
		dao = new FriendListDaoimpl();
	}
	
	/*
	 * 	搜尋陌生人
	 */
	public MemberBean searchStranger(String memberAccount ) {
		MemberDaoimpl memberDao = new MemberDaoimpl();
		MemberBean theStranger = null;
		theStranger = memberDao.selectByKey("ACCOUNT", memberAccount);
//		if (theStranger != null) {
//			FriendListBean checkList = new FriendListBean();
//			checkList.setAccept_M_ID(theStranger.getMember_id());
//		}
		return theStranger;
	}
	
	/*
	 * 	取得FriendList關係
	 */
	public String getFriendRelation(FriendListBean checkList) {
		String friendRelation = null;
<<<<<<< HEAD
		FriendListBean relation = new FriendListDaoimpl().selectRelation(checkList);
//System.out.println("checkList.acceptId: "+checkList.getAccept_M_ID());
//System.out.println("checkList.inviteId: "+checkList.getInvite_M_ID());
=======
		FriendListBean relation = dao.selectRelation(checkList);
>>>>>>> Justin_Branch
		if (relation == null) {
			if (checkList.getInvite_M_ID().equals(checkList.getAccept_M_ID()) ) {
				friendRelation = "myself";
			}else {
				friendRelation = "insert";
			}
		}else {
			int friendStatus = relation.getFriend_Status();
			switch (friendStatus) {
			case 1:	//已經是好友
				friendRelation = "wasFriend";
				break;
			case 2: //曾經拒絕過
				friendRelation = "insert";
				break;
			case 3: //好友狀態待審中
				String intvitId = checkList.getInvite_M_ID();
				String relationInviteId = relation.getInvite_M_ID();
				
//				System.out.println("intvitId: " + intvitId);
//				System.out.println("relationInviteId: " + relationInviteId);
				
				if (intvitId.equals(relationInviteId) ) {
//					System.out.println("intvitId == relationInviteId");
					friendRelation = "pedding";
				}else {
//					System.out.println(intvitId != relationInviteId);
					friendRelation = "response";
				}
				break;
			default:
				break;
			}
		}
		
		
		
		return friendRelation;
	}
	
	
	/*
	 * 	同意/拒絕 FriendStatus
	 */
	public int changeFriendList(FriendListBean bean, String action) {
		int resultCode = 0;
		FriendListBean relation = new FriendListDaoimpl().selectRelation(bean);
		bean.setuId(relation.getuId());
		switch (action) {
		case "clickAgree":
			bean.setFriend_Status(1);
			break;
		case "clickDecline":
			bean.setFriend_Status(2);
			break;
		default:
			bean.setFriend_Status(-1);
			break;
		}
		resultCode = dao.update(bean);
		return resultCode;
	}
	
	/*
	 * 刪好友
	 */
	public  int deletRelation(FriendListBean bean) {
		String mainId = bean.getInvite_M_ID();
		String acceptId = bean.getAccept_M_ID();
		int result = dao.deletaByKey(mainId, acceptId);
		return result;
	}
}
