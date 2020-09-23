package web.jome17.jome_notify.friend_invitation.find_new_friend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
			e.printStackTrace();
		}
		return findNewFriend;
	}

	@Override
	public List<FindNewFriend> findFriendRecord(String id1, String id2) {
		String sql = "SELECT * FROM Tep101_Jome17.FRIEND_LIST "
				+ "WHERE INVITE_M_ID = ? and ACCEPT_M_ID = ? or INVITE_M_ID = ? and ACCEPT_M_ID = ?;";
		List<FindNewFriend> friendRecords = new ArrayList<FindNewFriend>();
		try (
			Connection connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(sql);
		){
			pstmt.setString(1, id1);
			pstmt.setString(2, id2);
			pstmt.setString(3, id2);
			pstmt.setString(4, id1);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int uId = rs.getInt("UID");
				String inviteId = rs.getString("INVITE_M_ID");
				String acceptId = rs.getString("ACCEPT_M_ID");
				int friendStatus = rs.getInt("FRIEND_STATUS");
				findNewFriend = new FindNewFriend(uId, friendStatus, inviteId, acceptId);
				friendRecords.add(findNewFriend);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return friendRecords;
	}

	@Override
	public FindNewFriend findByUId(int uId) {
		
		return null;
	}

	@Override
	public byte[] getImage(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
