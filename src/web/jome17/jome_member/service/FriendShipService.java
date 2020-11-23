package web.jome17.jome_member.service;

import java.util.ArrayList;
import java.util.List;

import web.jome17.jome_member.bean.FriendListBean;
import web.jome17.jome_member.bean.MemberBean;
import web.jome17.jome_member.dao.CommonDao;
import web.jome17.jome_member.dao.FriendListDaoimpl;
import web.jome17.jome_member.dao.MemberDaoimpl;

public class FriendShipService {
	private CommonDao<FriendListBean, String> dao;

	public FriendShipService() {
		dao = new FriendListDaoimpl();
	}
	
	/*
	 * 	查詢好友列表
	 */
	public List<MemberBean> selectMyFriend(String memberId) {
		List<MemberBean> friends = new ArrayList<MemberBean>();
		List<FriendListBean> friendShips  = null;
		friendShips = dao.selectAll(memberId);
		
		for(FriendListBean friendShip : friendShips) {
			if(friendShip.getFriend_Status() == 1) {
				MemberBean friend = new MemberBean();
				if(friendShip.getInvite_M_ID().equals(memberId)) {
					friend.setMemberId(friendShip.getAccept_M_ID());
					friend.setNickname(friendShip.getAcceptName());
				}
				if(friendShip.getAccept_M_ID().equals(memberId)) {
					friend.setMemberId(friendShip.getInvite_M_ID());
					friend.setNickname(friendShip.getInviteName());
				}
				friends.add(friend);
			}
		}
		return friends;
	}
	
	/*
	 *  查詢好友人數
	 */
	public String selectFriendCount(String memberId) {
		return dao.getCount(memberId);
	}
	
	/*
	 * 	搜尋陌生人
	 */
	public MemberBean searchStranger(FriendListBean checkList) {
		MemberDaoimpl memberDao = new MemberDaoimpl();
		MemberBean theStranger = null;
		if(dao.selectRelation(checkList) == null) {
			theStranger = memberDao.selectByKey("memberId", checkList.getInvite_M_ID());
		}
		return theStranger;
	}
	
	/*
	 * 確認關係
	 */
	public int identifyRelation(String mainId, String friendId) {
		FriendListBean identifyBean = new FriendListBean(mainId, friendId);
		FriendListBean resultFriendList = dao.selectRelation(identifyBean);
		int relationCode = -1;
		if(resultFriendList != null) {
			int status = resultFriendList.getFriend_Status();
			switch (status) {
			case 1:
				relationCode = 1;
				break;
			case 2:
				relationCode = 2;
				break;
			case 3:
				if(mainId.equals(resultFriendList.getInvite_M_ID())) {
					relationCode = 3;
				}else {
					relationCode = 4;	//別人邀請，等待我回應
				}
				break;
			default:
				break;
			}
		}
		return relationCode;
	}
	
	/*	
	 * 	變更好友關係(含新增)		
	 */
	public int changeFriendShip(FriendListBean inviteList) {
		FriendListBean friListBean = null;
		int ResultInvite = -1;
		friListBean = dao.selectRelation(inviteList);
		if(friListBean == null) {
			if(dao.insert(friListBean) == 1) {
				ResultInvite = 3;					//待審
			}
		}else {
			int friendStatus = friListBean.getFriend_Status();
			friListBean.setInvite_M_ID(inviteList.getInvite_M_ID());
			friListBean.setAccept_M_ID(inviteList.getAccept_M_ID());
			switch(friendStatus){
				case 1:
					friListBean.setFriend_Status(2);
					if(dao.update(friListBean) == 1) {
						ResultInvite = 2;					//同意→拒絕
					}
					break;
				case 2:
					friListBean.setFriend_Status(3);		//拒絕→待審
					if(dao.update(friListBean) == 1) {
						ResultInvite = 3;					//邀請為好友
					}
					break;
				case 3:
					friListBean.setFriend_Status(1);		//待審→同意
					if(dao.update(friListBean) == 1) {
						ResultInvite = 1;					//同意為好友
					}
					break;
				default:
					ResultInvite = 0;						//維持原狀
					break;
			}
		}
		return ResultInvite;
	}
	
}	
