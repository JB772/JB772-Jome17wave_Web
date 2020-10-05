package web.jome17.jome_notify.service;

import java.util.ArrayList;
import java.util.List;

import web.jome17.jome_member.bean.FriendListBean;
import web.jome17.jome_member.bean.ScoreBean;
import web.jome17.jome_notify.bean.AttenderBean;
import web.jome17.jome_notify.bean.Notify;
import web.jome17.jome_notify.dao.NotifyDaoImpl;

public class NotifyService {
	
	/*
	 * 組合顯示句子
	 * 	收到NotifyList，迭代一個一個取出加工
	 * 								加工分類一：依type分類，去作相對應的方法，取得需要的材料
	 * 								加工分類二：依不同材料分類作對應方法，並依材料內容造相關句子
	 */
	public List<Notify> getNotifiesWordList(List<Notify> notifiesList) {
		AttenderBean attenderBean = new AttenderBean();
		FriendListBean friendListBean = new FriendListBean();
		Notify notify = new Notify();
		List<Notify> notifiesWordList = new ArrayList<>();
		String groupName = null;
		String notificationDetail = null;
		for (Notify no : notifiesList) {
			int type = no.getType();
//System.out.println("type: " + type);
			int notificationBody = no.getNotificationBody();
//System.out.println("notificationBody: " + notificationBody);
			switch (type) {
				case 1: 	//揪團
					attenderBean = new NotifyDaoImpl().getAttenderBean(notificationBody);
//System.out.println("attenderBean: " + attenderBean.getGroupName());
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
//System.out.println("attenderBean != null");
				groupName = attenderBean.getGroupName();
//System.out.println("groupName: "+ groupName);
//System.out.println("AttendStatus: "+attenderBean.getAttendStatus());
				switch (attenderBean.getAttendStatus()) {
					case 0:		//自行離開
						//發通知給團長：掰～我自己走摟～:)
						break;
					case 1:		//同意
//System.out.println("case 1");
						notificationDetail = "成功加入 "+ groupName + " 揪團活動";
						break;
					case 2:		//被拒絕
						notificationDetail = "失敗加入 "+ groupName + " 揪團活動";
						break;
					case 3:		//我是團員我申請入團，待審查中
						//發通知給團長：嗨～我想加入啦～:)
						break;
					default:
						break;
				}
			}else if (friendListBean != null) {
				String acceptName = friendListBean.getAcceptName();
				switch (friendListBean.getFriend_Status()) {
					case 1: 	//	同意
						notificationDetail = acceptName + " 已同意你的好友邀請";
						break;
					default:
						break;
				}
			}else if (groupName != null) {
				notificationDetail = groupName + " 已結束，你喜歡嗎？";
			}
			
//System.out.println("notificationDetail: "+ notificationDetail);
			no.setNotificationDetail(notificationDetail);
			notifiesWordList.add(no);
		}
		
		return notifiesWordList;
	}

}
