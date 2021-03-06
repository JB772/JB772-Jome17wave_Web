package web.jome17.jome_notify.service;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import web.jome17.jome_member.bean.FriendListBean;

import web.jome17.jome_member.bean.PersonalGroup;
import web.jome17.jome_member.bean.ScoreBean;
import web.jome17.jome_member.dao.AttenderDaoimpl;
import web.jome17.jome_member.dao.FriendListDaoimpl;
import web.jome17.jome_notify.bean.AttenderBean;
import web.jome17.jome_notify.bean.Notify;
import web.jome17.jome_notify.dao.FriendInvitationDaoImpl;
import web.jome17.jome_notify.dao.NotifyDaoImpl;

public class NotifyService {

	/*
	 * 組合顯示句子 收到NotifyList，迭代一個一個取出加工 加工分類一：依type分類，去作相對應的方法，取得需要的材料
	 * 加工分類二：依不同材料分類作對應方法，並依材料內容造相關句子
	 */
	public List<Notify> getNotifiesWordList(List<Notify> notifiesList, String myId) {
//System.out.println("NotifyService 裡的 getNotifiesWordList");
		AttenderBean attenderBean = new AttenderBean();
		
		FriendListBean friendListBean = new FriendListBean();
		
		Notify notify = new Notify();
		List<Notify> notifiesWordList = new ArrayList<>();
		
		for (Notify no : notifiesList) {
			int type = no.getType();
			attenderBean = null;
			friendListBean = null;
			String groupName = null;
			String notificationDetail = null;
			String notificationBody = no.getNotificationBody();
			switch (type) {
			case 1: // 揪團
				attenderBean = new NotifyDaoImpl().getAttenderBean(notificationBody);
				break;
			case 2: // 好友
				friendListBean = new NotifyDaoImpl().getFriendListBean(notificationBody);
				break;
			case 3: // 評分
				groupName = new NotifyDaoImpl().getGroupBean(notificationBody).getGroupName();
				break;
			default:
				break;
			}

			if (attenderBean != null) {
				groupName = attenderBean.getGroupName();
				switch (no.getBodyStatus()) {
				case 0: // 自行離開
					// 發通知給團長：掰～我自己走摟～:)
					if (!attenderBean.getMemberId().equals(myId)) {
						String memberName = attenderBean.getMemberName();
						notificationDetail = memberName + " 已離開 " + groupName + " 揪團活動";
					} else {
						notificationDetail = "你已離開 " + groupName + " 揪團活動";
					}
					break;
				case 1: // 同意
					if (attenderBean.getMemberId().equals(myId)) {
						notificationDetail = "成功加入 " + groupName + " 揪團活動";
					}
					break;
				case 2: // 被拒絕
					if (attenderBean.getMemberId().equals(myId)) {
						notificationDetail = "失敗加入 " + groupName + " 揪團活動";
					}
					break;
				case 3: // 我是團員我申請入團，待審查中
					// 發通知給團長：嗨～我想加入啦～:)
					String memberName = attenderBean.getMemberName();
					notificationDetail = memberName + " 要求加入 " + groupName + " 揪團活動";

					break;
				default:
					break;
				}
			} else if (friendListBean != null) {
				String acceptName = friendListBean.getAcceptName();
				switch (no.getBodyStatus()) {
				case 1: // 同意
					if (friendListBean.getInvite_M_ID().equals(no.getMemberId())) {
						notificationDetail = acceptName + " 已同意你的好友邀請";
					} else {
						notificationDetail = "你與 " + friendListBean.getInviteName() + " 已成為好友";
					}

					break;
				case 3: // 等待審核中
					if (friendListBean.getInvite_M_ID().equals(no.getMemberId())) {
						break;
					} else {
						notificationDetail = friendListBean.getInviteName() + " 發送好友邀請給你";
					}

					break;
				default:
					break;
				}
			} else if (groupName != null) {
				notificationDetail = groupName + " 已結束，你喜歡嗎？";
			}

//System.out.println("notificationDetail: "+ notificationDetail);
			if (notificationDetail != null) {
				no.setNotificationDetail(notificationDetail);
				notifiesWordList.add(no);
			} else {
				break;
			}

		}

		return notifiesWordList;
	}

	/*
	 * 組合Body代碼1 的資料 收到attenderNo， 用attenderNo得到GroupId回傳
	 */
	public String getGroupId(String attenderNo) {
		// TODO Auto-generated method stub
//		return new AttenderDaoimpl().getAttenderBean.getGroupId;
		return null;
	}

	/*
	 * 組合Body代碼2 的資料 收到uID與自己的ID，
	 * 用uId得到FriendListBean，再用自己的Id與FriendListBean做比對，取得對方的Id回傳
	 */
	public String getOtherMemberId(String uId, String myselfId) {

		FriendListBean bean = new FriendInvitationDaoImpl().selectByKey("UID", uId);
//System.out.println("Invite_M_ID(): " + bean.getInvite_M_ID());
		String otherMemberId = bean.getInvite_M_ID().equals(myselfId) ? bean.getAccept_M_ID() : bean.getInvite_M_ID();
		return otherMemberId;
	}

// ----------------------- 以下為無交易控制區 ----------------------- //
//	/*
//	 * 新增通知訊息: 好友類型通知: 同意成為好友(雙方有通知)
//	 * 
//	 */
//	public int insertFriendNoti(FriendListBean agreeBean) {
//
////		// 新增通知訊息 給對方
//		Notify notify = new Notify();
//		notify.setType(2);
//		notify.setNotificationBody(agreeBean.getuId());
//		notify.setBodyStatus(1);
//		notify.setMemberId(agreeBean.getInvite_M_ID());
//		int notifyRC = new NotifyDaoImpl().insert(notify);
//
//		// 新增通知訊息 給自己
//		notify.setMemberId(agreeBean.getAccept_M_ID());
//		notifyRC = new NotifyDaoImpl().insert(notify);
//		
//		notify.setBodyStatus(3);
//		int deleteId = new NotifyDaoImpl().findNotificationId(notify);
//// System.out.println("deleteId: " + deleteId);
//		notifyRC = new NotifyDaoImpl().delete(deleteId);
//
//		return notifyRC;
//	}
//
//	/*
//	 * 新增通知訊息: 揪團類型通知 
//	 * 		魯夫 要求加入 夏天來了 的揪團活動。 我是團員，我申請加入，然後要發通知給團長。 
//	 * 		成功/失敗加入 夏天來囉的揪團活動。我是團長，我審核申請後，要發通知給申請人。 
//	 * 		魯夫 離開了 夏天來囉 的揪團活動。當團員自行離開，或是遭到踢除的時候，團長會收到通知。 
//	 * 		你已離開夏天來囉 的揪團活動。團員自己離開，或是遭到踢除的時候，團員會收到通知。
//	 */
//	public int insertGroupNoti(PersonalGroup attenderBean) {
//		int notifyRC = -1;
//		int deleteRC = -1;
//		//用attenderBean搜尋團長Id(captainId)
//		String captainId = new AttenderDaoimpl().selectByGroupId(attenderBean.getGroupId()).getMemberId();
//		// 新增通知訊息給團員(notify)
//		Notify notify = new Notify();
//		notify.setType(1);
//		notify.setNotificationBody(attenderBean.getAttenderId());
//		notify.setBodyStatus(attenderBean.getAttenderStatus());
//		// 新增通知訊息給團長(notifyCaptain)
//		Notify notifyCaptain = notify;
//		notifyCaptain.setMemberId(captainId);
//		if(attenderBean.getAttenderStatus() == 0 || attenderBean.getAttenderStatus() == 3) {
//			notifyRC = new NotifyDaoImpl().insert(notifyCaptain);
//		}else {
//			notify.setMemberId(attenderBean.getMemberId());
//		}
//		
//		if (notifyRC != 0 && attenderBean.getAttenderStatus() != 3) {
//			notifyRC = new NotifyDaoImpl().insert(notify);
//		}
//		
//
//		// 當狀態轉為離開(0)時，刪除舊得通知訊息： 成功加入 夏天來囉 的揪團活動 ＆＆ 魯夫 要求加入 夏天來了 的揪團活動。
//		if (attenderBean.getAttenderStatus() == 0) {
//			notifyCaptain.setBodyStatus(3);
//			int deleteCaptainNotiId = new NotifyDaoImpl().findNotificationId(notifyCaptain);
//			notify.setBodyStatus(1);
//			int deleteMyselfNotiId = new NotifyDaoImpl().findNotificationId(notify);
//			deleteRC = new NotifyDaoImpl().delete(deleteCaptainNotiId);
//			deleteRC = new NotifyDaoImpl().delete(deleteMyselfNotiId);
//		}
//		
//		if (notifyRC == 1 && deleteRC == 1) {
//			notifyRC = 1;
//		}else {
//			notifyRC = -2;
//		}
//	
//		return notifyRC;
//	}
	
	// ----------------------- 以下為有交易控制區 ----------------------- //
	
	/*
	 * 新增通知訊息 - 2. 好友類型通知: 同意成為好友(雙方有通知)，使用交易控制的daoImpl
	 * 
	 */
	public int insertBothNotiForBeFriend(FriendListBean agreeBean) {
		//拿到資料庫裡的關係資料，status還是3
		FriendListBean relationBean = new FriendListDaoimpl().selectRelation(agreeBean);
		//組裝完整的Bean，準備做update
		agreeBean.setuId(relationBean.getuId());
		agreeBean.setFriend_Status(1);
		//同意成為好友，新增notify雙方通知
		int resultCode = new NotifyDaoImpl().insert2Noti(agreeBean);
		if (resultCode == 1) {
			//新增同意通知成功，做好友關係update，並刪除待審的通知訊息
			resultCode = new FriendListDaoimpl().changeNotiForFriendUpdate(agreeBean);
		}else {
			resultCode = -1;
			return resultCode;
		}
		
		if (resultCode == 1 ) {
			return resultCode;
		}else {
			//好友關係update與刪除待審的noti失敗：做刪除前面新增的兩則同意訊息！
//			int deleteRC = new NotifyDaoImpl().deleteForFriendUpdate(agreeBean);
			return resultCode;
		}
	}
	
	/*
	 * 新增通知訊息 - 2. 好友類型通知: 邀請你成為好友(被邀請方單方有通知)，使用交易控制的daoImpl
	 * 
	 */
	public int insertNotiForFriendInsert(FriendListBean checkList) {
		// checkList裡inviteId是自己，acceptId是對方，friendStatus是3(待審)
		int resultCode = -1;
		// relation是資料庫裡的資料
		FriendListBean relation = new FriendListDaoimpl().selectRelation(checkList);
		if (relation == null) {
			//沒有任何資料，做新增資料
			resultCode = new FriendListDaoimpl().insert(checkList); //要改成有交易控制的insert
//System.out.println("relation == null 且 resultCode: "+resultCode);
		}else {
			//已有資料，做更新資料
			checkList.setuId(relation.getuId());
			resultCode = new FriendListDaoimpl().changeNotiForFriendUpdate(checkList);
//System.out.println("relation != null 且 resultCode: "+resultCode);
		}
		return resultCode;
	}


	/*
	 * 新增通知訊息 - 3. 評分類型通知: 傳入該groupId所有的attender名單，建立評分通知，回傳成功建立資料筆數，使用交易控制的daoImpl
	 * 
	 */
	public int insertNotiForRating(List<PersonalGroup> attenders) {
		List<Notify> ratingNotifies = new ArrayList<Notify>();
		for (PersonalGroup personalGroup : attenders) {
			Notify notifyBean = new Notify();
			notifyBean.setType(3);
			notifyBean.setNotificationBody(personalGroup.getGroupId());
			notifyBean.setMemberId(personalGroup.getMemberId());
			ratingNotifies.add(notifyBean);
		}
		return new NotifyDaoImpl().insertNotiForRating(ratingNotifies);
	}
	
}

