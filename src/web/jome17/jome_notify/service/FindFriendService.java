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
		FriendListBean relation = new FriendListDaoimpl().selectRelation(checkList);
		if (relation == null) {
			System.out.println("relation == null");
			friendRelation = "insert";
		}else {
			System.out.println("relation != null");
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
				if (intvitId == relationInviteId) {
					friendRelation = "pedding";
				}else {
					friendRelation = "response";
				}
			default:
				break;
			}
		}
		
		return friendRelation;
	}
	
	public int changeFriendList(FriendListBean checkList) {
		int changeRelationCode = 0;
		FriendListBean relation = dao.selectRelation(checkList);
		if (relation == null) {
			if (dao.insert(checkList) == 1) {
				changeRelationCode = 3;
			}else {
				changeRelationCode = -1;
			}
		}else {
			int friendStatus = relation.getFriend_Status();
			switch (friendStatus) {
			case 1:	//已經是好友
				changeRelationCode = 1;
				break;
			case 2: //曾經拒絕過
				int result = dao.update(relation);
				if (result == 1) {
					changeRelationCode = 4;
				}else {
					changeRelationCode = -2;
				}
				break;
			case 3: //好友狀態待審中
				String intvitId = new FriendListBean().getInvite_M_ID();
				String relationInviteId = relation.getInvite_M_ID();
				if (intvitId == relationInviteId) {
					changeRelationCode = 2;
				}else {
					changeRelationCode = 5;
				}
			default:
				break;
			}
		}
		return changeRelationCode;
	}

}
