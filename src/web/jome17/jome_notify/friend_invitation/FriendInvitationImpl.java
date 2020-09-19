package web.jome17.jome_notify.friend_invitation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

public class FriendInvitationImpl implements FriendInvitationDao{
	DataSource  dataSource;
	

	@Override
	public int insert(FriendInvitation friendInvitation) {
		
		return 0;
	}

	@Override
	public int update(FriendInvitation friendInvitation) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<FriendInvitation> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FriendInvitation> findById(int id) {
		FriendInvitation friendInvitation = new FriendInvitation();
		String sql = "SELECT * FROM Tep101_Jome17.FRIEND_LIST "
				+ "WHERE INVITE_M_ID =? and ACCEPT_M_ID =? or INVITE_M_ID =? and ACCEPT_M_ID =?;";
		try (
			Connection connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(sql);
		){
			
			pstmt.setInt(1, friendInvitation.getInviteMId());
			pstmt.setInt(2, friendInvitation.getAcceptMId());
			pstmt.setInt(3, friendInvitation.getAcceptMId());
			pstmt.setInt(4, friendInvitation.getInviteMId());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	

}
