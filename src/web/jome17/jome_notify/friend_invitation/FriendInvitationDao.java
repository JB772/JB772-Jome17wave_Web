package web.jome17.jome_notify.friend_invitation;

import java.util.List;

import org.apache.jasper.tagplugins.jstl.core.ForEach;

public interface FriendInvitationDao {
	int insert(FriendInvitation friendInvitation);
	int update(FriendInvitation friendInvitation);
	List<FriendInvitation> getAll();
	List<FriendInvitation>findById();
}


