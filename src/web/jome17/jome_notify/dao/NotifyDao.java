package web.jome17.jome_notify.dao;

import java.util.List;

import web.jome17.jome_member.bean.FriendListBean;
import web.jome17.jome_group.Group;
import web.jome17.jome_member.bean.ScoreBean;
import web.jome17.jome_notify.bean.AttenderBean;
import web.jome17.jome_notify.bean.Notify;

public interface NotifyDao {
	int insert(Notify bean);
	int deletaByKey(String buildDate);
	List<Notify> getAll(String memberId);
	FriendListBean getFriendListBean (int notificationBody);
	Group getGroupBean (int notificationBody);
	List<ScoreBean> getScoreList (int notificationBody, String memberId);
	AttenderBean getAttenderBean (int notificationBody);
}
