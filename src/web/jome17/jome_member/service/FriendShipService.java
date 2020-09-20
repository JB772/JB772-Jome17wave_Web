package web.jome17.jome_member.service;

import web.jome17.jome_member.bean.FriendListBean;
import web.jome17.jome_member.dao.CommonDao;
import web.jome17.jome_member.dao.FriendListDaoimpl;

public class FriendShipService {
	private CommonDao<FriendListBean, String> dao;

	public FriendShipService() {
		dao = new FriendListDaoimpl();
	}
	
	/*	新增好友
	 * 	1.檢查兩者在表上是否有過關係
	 * 		false	: 新增;
	 * 		true	: 檢查 FRIEND_STATUS 更新;
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
