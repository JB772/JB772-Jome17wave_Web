package web.jome17.jome_notify.friend_invitation.find_new_friend;

import java.util.List;

public interface FindNewFriendDao {
	//用account查詢新朋友id
	FindNewFriend findByAccunt(String account);
	//檢查FriendList紀錄
	List<FindNewFriend> findFriendRecord(String id1, String id2);
	//檢查好友狀態
	FindNewFriend findByUId(int uId);
	//取得照片
	byte[] getImage(String id);
}
