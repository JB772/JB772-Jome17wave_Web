package web.jome17.jome_notify.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import web.jome17.jome_member.bean.FriendListBean;
import web.jome17.jome_member.dao.CommonDao;
import web.jome17.main.ServiceLocator;

public class FriendInvitationDaoImpl implements CommonDao<FriendListBean, String> {
	private DataSource dataSource;
	
	public FriendInvitationDaoImpl() {
		dataSource = ServiceLocator.getInstance().getDataSource();
	}
	
	
	//查詢 邀請列表
	@Override
	public List<FriendListBean> selectAll(String memberId) {
		String sql =  "SELECT f.*, m.NICKNAME as INVITE_M_NANE "
					+ "FROM Tep101_Jome17.FRIEND_LIST f "
					+ "left join Tep101_Jome17.MEMBERINFO m "
						+ "on f.INVITE_M_ID = m.ID "
					+ "WHERE ACCEPT_M_ID = ? and FRIEND_STATUS = 3 "
					+ "order by BUILD_DATE desc;";
		List<FriendListBean> invitations = new ArrayList<FriendListBean>();
		try (
			Connection connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(sql);
		){
			pstmt.setString(1, memberId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				FriendListBean bean = new FriendListBean();
				bean.setuId(rs.getInt("UID"));
				bean.setInvite_M_ID(rs.getString("Invite_M_ID"));
				bean.setInviteName(rs.getString("INVITE_M_NANE"));
				bean.setAccept_M_ID(rs.getString("Accept_M_ID"));
				bean.setFriend_Status(rs.getInt("Friend_Status"));
//				bean.setModify_Date(rs.getDate("Modify_Date"));
				invitations.add(bean);
			}
			return invitations;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 查詢FriendList列表
	@Override
	public FriendListBean selectByKey(String keyword, String key) {
		FriendListBean bean = new FriendListBean();
		String sql = "";
		if (keyword.equals("UID")) {
			sql =  "SELECT * FROM Tep101_Jome17.FRIEND_LIST where UID = ?;";
		}
		
	try (
		Connection connection = dataSource.getConnection();
		PreparedStatement pstmt = connection.prepareStatement(sql);
	){
		pstmt.setInt(1, Integer.valueOf(key));
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			bean.setuId(rs.getInt("UID"));
			bean.setInvite_M_ID(rs.getString("Invite_M_ID"));
			bean.setAccept_M_ID(rs.getString("Accept_M_ID"));
			bean.setFriend_Status(rs.getInt("Friend_Status"));
		}
		return bean;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
	}
	

	/*
	 * 以下為目前不需要實作的方法
	 */
	@Override
	public int insert(FriendListBean bean) {
		// TODO Auto-generated method stub
		return 0;
	}

	


	@Override
	public FriendListBean selectRelation(FriendListBean bean) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int update(FriendListBean bean) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deletaByKey(String key, String key1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] getImage(String acconut) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getCount(String memberId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<FriendListBean> selectAllNoKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
