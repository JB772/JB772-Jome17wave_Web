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
	 * 		true	: 檢查FRIEND_STATUS 更新;
	 */
	public int beMyFriend(FriendListBean inviteList) {
		FriendListBean friListBean = null;
		int ResultInvite = -1;
		friListBean = dao.selectRelation(inviteList);
		if(friListBean == null) {
			ResultInvite = dao.insert(inviteList);
		}else {
			int friendStatus = friListBean.getFriend_Status();
			friListBean.setInvite_M_ID(inviteList.getInvite_M_ID());
			friListBean.setAccept_M_ID(inviteList.getAccept_M_ID());
			switch(friendStatus){
				case 2:
					friListBean.setFriend_Status(3);		//拒絕→邀請：待審
					if(dao.update(friListBean) == 1) {
						ResultInvite = 2;					//邀請為好友
					}
					break;
				case 3:
					friListBean.setFriend_Status(1);		//待審→同意
					if(dao.update(friListBean) == 1) {
						ResultInvite = 1;					//同意為好友
					}
					break;
				default:
					ResultInvite = 0;						//本來就是好友
					break;
			}
		}
		return ResultInvite;
	}
}
