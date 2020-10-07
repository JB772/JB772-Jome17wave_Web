package web.jome17.jome_notify.dao;

import java.util.List;

import web.jome17.jome_member.bean.FriendListBean;
import web.jome17.jome_group.Group;
import web.jome17.jome_member.bean.ScoreBean;
import web.jome17.jome_notify.bean.AttenderBean;
import web.jome17.jome_notify.bean.Notify;

public interface NotifyDao {
	//新增
	int insert(Notify bean);
	//刪除
	int delete(int notificationId);
	//找到要刪除的那筆資料
	int findNotificationId(Notify bean);
	
	//查詢(個人的)通知列表
	List<Notify> getAll(String memberId);
	//用 UID 取得 FriendListBean 物件 (好友狀態紀錄)
	FriendListBean getFriendListBean (int notificationBody);
	//用 Attender_No 取得 AttenderBean 物件 (單則揪團成功/失敗的Bean)
	AttenderBean getAttenderBean (int notificationBody);
	//用 Group_ID 與 memberId 取得 ScoreList  (評分列表)
	List<ScoreBean> getScoreList (int notificationBody, String memberId);
	
	//用 Group_ID 取得 GroupBean 物件 (單則揪團資料)
	Group getGroupBean (int notificationBody);
	
	
}
