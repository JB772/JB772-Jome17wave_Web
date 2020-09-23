package web.jome17.jome_notify.friend_invitation;

import java.util.List;

import org.apache.jasper.tagplugins.jstl.core.ForEach;

public interface FriendInvitationDao {
//	//新增好友名單
//	int insert(FriendInvitation friendInvitation);
//	//修改好友名單
//	int update(FriendInvitation friendInvitation);
	//列出好友邀請名單
	List<FriendInvitation> getAll();
	//找到自己被邀請的紀錄
	List<FriendInvitation>findById(String id);
	//取得照片
	byte[] getImage(String id);
	
	
}


