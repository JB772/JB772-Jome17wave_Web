package web.jome17.jome_member.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import web.jome17.jome_member.bean.FriendListBean;
import web.jome17.main.ServiceLocator;

public class FriendListDaoimpl implements CommonDao<FriendListBean, String> {
	private DataSource dataSource;
	
		
	public FriendListDaoimpl() {
		dataSource = ServiceLocator.getInstance().getDataSource();
	}
	
	//查詢筆數
	@Override
	public String getCount(String memberId) {
		String sql = "select count(*) "
					+ "from Tep101_Jome17.FRIEND_LIST "
					+ "where (INVITE_M_ID = ? or ACCEPT_M_ID = ?) "
						+ "and FRIEND_STATUS = 1;";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, memberId);
			pstmt.setString(2, memberId);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				String countResult = "";
				countResult = String.valueOf(rs.getInt(1));
				return countResult;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//新增
	@Override
	public int insert(FriendListBean friendList) {
		String sql = "insert into Tep101_Jome17.FRIEND_LIST(INVITE_M_ID, ACCEPT_M_ID) "
					+ "values	(?,?) ;";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, friendList.getInvite_M_ID());
			pstmt.setString(2, friendList.getAccept_M_ID());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	//更新
	@Override
	public int update(FriendListBean bean) {
		String sql = "update Tep101_Jome17.FRIEND_LIST "
				+ "SET INVITE_M_ID = ?, ACCEPT_M_ID = ?, FRIEND_STATUS = ? "
				+ "WHERE UID = ? ;";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);){
			pstmt.setString(1, bean.getInvite_M_ID());
			pstmt.setString(2, bean.getAccept_M_ID());
			pstmt.setInt(3, bean.getFriend_Status());
			pstmt.setInt(4, bean.getuId());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	//查詢(查個人好友列表含名字)
	@Override
	public FriendListBean selectByKey(String keyword, String memberId) {
		String sql = "select " 
						+ "f.UID,"  
						+ "f.INVITE_M_ID,"
						+ "m.NICKNAME,"
						+ "f.ACCEPT_M_ID,"
						+ "m1.NICKNAME," 
						+ "f.FRIEND_STATUS," 
						+ "f.MODIFY_DATE"
					+ "from" 
						+ "FRIEND_LIST f" 
						+ "left join MEMBERINFO m" 
						+ "on f.INVITE_M_ID = m.ID" 
							+ "join MEMBERINFO m1" 
							+ "on f.ACCEPT_M_ID = m1.ID" 
					+ "where" 
						+ "f.INVITE_M_ID = ? or" 
						+ "f.ACCEPT_M_ID = ?" 
					+ "order by" 
						+ "MODIFY_DATE desc;";
		FriendListBean friendList = null;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, memberId);
			pstmt.setString(2, memberId);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				friendList = new FriendListBean();
				friendList.setuId(rs.getInt(1));
				friendList.setInvite_M_ID(rs.getString(2));
				friendList.setInviteName(rs.getString(3));
				friendList.setAccept_M_ID(rs.getString(4));
				friendList.setAcceptName(rs.getString(5));
				friendList.setFriend_Status(rs.getInt(6));
				friendList.setModify_Date(rs.getDate(7));
				return friendList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return friendList;
	}
	
	//查詢(確認兩id之間是否有建立關係)
	@Override
	public FriendListBean selectRelation(FriendListBean checkList) {
		FriendListBean friendList = null;
		String Id1 = checkList.getInvite_M_ID();
		String Id2 = checkList.getAccept_M_ID();
		String sql = "SELECT * FROM Tep101_Jome17.FRIEND_LIST "
				+ "WHERE INVITE_M_ID = ? and ACCEPT_M_ID = ? or INVITE_M_ID = ? and ACCEPT_M_ID = ?;";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, Id1);
			pstmt.setString(2, Id2);
			pstmt.setString(3, Id2);
			pstmt.setString(4, Id1);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				friendList = new FriendListBean();
				friendList.setuId(rs.getInt("UID"));
				friendList.setInvite_M_ID(rs.getString("INVITE_M_ID"));
				friendList.setAccept_M_ID(rs.getString("ACCEPT_M_ID"));
				friendList.setFriend_Status(rs.getInt("FRIEND_STATUS"));
				friendList.setModify_Date(rs.getDate("MODIFY_DATE"));
				return friendList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return friendList;
	}
	

<<<<<<< HEAD
	
	//查出memberId的朋友列表（好友列表）
=======
	//查出memberId的朋友列表
>>>>>>> 29c476a9dd36c8d04f02a096efdbd32959d4f4d0
	@Override
	public List<FriendListBean> selectAll(String memberId) {
		String sql = "select "
					+ "f.UID, "
					+ "f.INVITE_M_ID, "
					+ "m.NICKNAME, "
					+ "f.ACCEPT_M_ID, "
					+ "m1.NICKNAME, "
					+ "f.FRIEND_STATUS, "
					+ "f.MODIFY_DATE "
				+ "from "
					+ "Tep101_Jome17.FRIEND_LIST f "
					+ "left join MEMBERINFO m "
						+ "on f.INVITE_M_ID = m.ID "
							+ "join MEMBERINFO m1 "
							+ "on f.ACCEPT_M_ID = m1.ID "
				+ "where "
					+ "f.INVITE_M_ID = ? or "
					+ "f.ACCEPT_M_ID = ? "
				+ "order by "
					+ "MODIFY_DATE desc;";
		try(Connection conn = dataSource.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);) {
				List<FriendListBean> friends = new ArrayList<FriendListBean>();
				FriendListBean friend = null;
				pstmt.setString(1, memberId);
				pstmt.setString(2, memberId);
				ResultSet rs = pstmt.executeQuery();
				while(rs.next()) {
					friend = new FriendListBean();
					friend.setuId(rs.getInt(1));
					friend.setInvite_M_ID(rs.getString(2));
					friend.setInviteName(rs.getString(3));
					friend.setAccept_M_ID(rs.getString(4));
					friend.setAcceptName(rs.getString(5));
					friend.setFriend_Status(rs.getInt(6));
					friend.setModify_Date(rs.getDate(7));
					friends.add(friend);
				}
				return friends;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
	}




/*
 * 以下為目前不需要實作的方法
 */
	

	
	@Override
	public byte[] getImage(String acconut) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int deletaByKey(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FriendListBean login(String account, String password) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
