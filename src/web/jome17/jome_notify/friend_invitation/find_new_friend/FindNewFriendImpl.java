package web.jome17.jome_notify.friend_invitation.find_new_friend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.sql.DataSource;


public class FindNewFriendImpl implements FindNewFriendDao{
	DataSource dataSource;
	FindNewFriend findNewFriend = null;
	
	@Override
	public FindNewFriend findByAccunt(String account) {
		String sql = "SELECT * FROM Tep101_Jome17.MEMBERINFO where ACCOUNT = ?;";
		FindNewFriend newFriend = null;
		try (
			Connection connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(sql);
		){
			pstmt.setString(1, account);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String memberId = rs.getString(1);
				String memberAccount = rs.getString(2);
				String memberName = rs.getString(4);
				findNewFriend = new FindNewFriend(memberId, memberName, memberAccount);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return findNewFriend;
	}

	@Override
	public List<FindNewFriend> findFriendRecord(int inviteId, int acceptId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FindNewFriend findByUId(int uId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getImage(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
