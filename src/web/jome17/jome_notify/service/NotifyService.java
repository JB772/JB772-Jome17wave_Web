package web.jome17.jome_notify.service;

import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.log.Log;

import sun.invoke.empty.Empty;
import web.jome17.jome_member.bean.FriendListBean;
import web.jome17.jome_member.bean.ScoreBean;
import web.jome17.jome_member.dao.AttenderDaoimpl;
import web.jome17.jome_notify.bean.AttenderBean;
import web.jome17.jome_notify.bean.Notify;
import web.jome17.jome_notify.dao.FriendInvitationDaoImpl;
import web.jome17.jome_notify.dao.NotifyDaoImpl;

public class NotifyService {
	
	/*
	 * 組合顯示句子
	 * 	收到NotifyList，迭代一個一個取出加工
	 * 								加工分類一：依type分類，去作相對應的方法，取得需要的材料
	 * 								加工分類二：依不同材料分類作對應方法，並依材料內容造相關句子
	 */
	public List<Notify> getNotifiesWordList(List<Notify> notifiesList, String myId) {
//System.out.println("NotifyService 裡的 getNotifiesWordList");
		AttenderBean attenderBean = new AttenderBean();
		attenderBean = null;
		FriendListBean friendListBean = new FriendListBean();
		friendListBean = null;
		Notify notify = new Notify();
		List<Notify> notifiesWordList = new ArrayList<>();
		String groupName = null;
		String notificationDetail = null;
		for (Notify no : notifiesList) {
			int type = no.getType();
			int notificationBody = no.getNotificationBody();
			switch (type) {
				case 1: 	//揪團
					attenderBean = new NotifyDaoImpl().getAttenderBean(notificationBody);
					break;
				case 2: 	//好友
					friendListBean = new NotifyDaoImpl().getFriendListBean(notificationBody);
					break;
				case 3: 	//評分
					groupName = new NotifyDaoImpl().getGroupBean(notificationBody).getGroupName();
					break;
				default:
					break;
			}
			
			if (attenderBean != null) {
				groupName = attenderBean.getGroupName();
				switch (no.getBodyStatus()) {
					case 0:		//自行離開
						//發通知給團長：掰～我自己走摟～:)
						if (!attenderBean.getMemberId().equals(myId)) {
							String memberName = attenderBean.getMemberName();
							notificationDetail = memberName + " 已退出 " + groupName + " 揪團活動";
						}else {
							notificationDetail = "你已退出 " + groupName + " 揪團活動"; 
						}
						break;
					case 1:		//同意
						if (attenderBean.getMemberId().equals(myId)) {
							notificationDetail = "成功加入 "+ groupName + " 揪團活動";
						}
						break;
					case 2:		//被拒絕
						if (attenderBean.getMemberId().equals(myId)) {
							notificationDetail = "失敗加入 "+ groupName + " 揪團活動";
						}
						break;
					case 3:		//我是團員我申請入團，待審查中
						//發通知給團長：嗨～我想加入啦～:)
							String memberName = attenderBean.getMemberName();
							notificationDetail = memberName + " 要求加入 " + groupName + " 揪團活動";

						break;
					default:
						break;
				}
			}else if (friendListBean != null) {
				String acceptName = friendListBean.getAcceptName();
				switch (no.getBodyStatus()) {
					case 1: 	//	同意
						if (friendListBean.getInvite_M_ID().equals(no.getMemberId())) {
							notificationDetail = acceptName + " 已同意你的好友邀請";
						}else {
							notificationDetail = "你與 " + friendListBean.getInviteName() + " 已成為好友";
						}
						
						break;
					case 3: 	//	等待審核中
						if (friendListBean.getInvite_M_ID().equals(no.getMemberId())) {
							break;
						}else {
							notificationDetail = friendListBean.getInviteName() + " 發送好友邀請給你";
						}
						
						break;
					default:
						break;
				}
			}else if (groupName != null) {
				notificationDetail = groupName + " 已結束，你喜歡嗎？";
			}
			
//System.out.println("notificationDetail: "+ notificationDetail);
			if (notificationDetail != null) {
				no.setNotificationDetail(notificationDetail);
				notifiesWordList.add(no);
			}else {
				break;
			}
			
		}
		
		return notifiesWordList;
	}
	
	/*
	 * 組合Body代碼1 的資料
	 * 	收到attenderNo，
	 * 			用attenderNo得到GroupId回傳
	 */
	public String getGroupId(String attenderNo) {
		// TODO Auto-generated method stub
//		return new AttenderDaoimpl().getAttenderBean.getGroupId;
		return null;
	}
	
	/*
	 * 組合Body代碼2 的資料
	 * 	收到uID與自己的ID，
	 * 			用uId得到FriendListBean，再用自己的Id與FriendListBean做比對，取得對方的Id回傳
	 */
	public String getOtherMemberId(String uId, String myselfId) {

		FriendListBean bean = new FriendInvitationDaoImpl().selectByKey("UID", uId);
//System.out.println("Invite_M_ID(): " + bean.getInvite_M_ID());
		String otherMemberId = bean.getInvite_M_ID().equals(myselfId) ? bean.getAccept_M_ID() : bean.getInvite_M_ID();
		return otherMemberId;
	}



	

}
