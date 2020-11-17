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
	
	//新增陌生人為好友
	@Override
	public int insert(FriendListBean friendList) {
		String sqlInsertFriend = "insert into Tep101_Jome17.FRIEND_LIST(INVITE_M_ID, ACCEPT_M_ID) "
								+ "values	(?,?) ;";
		String sqlseletctTableFriend = "select UID from Tep101_Jome17.FRIEND_LIST "
									+ "where INVITE_M_ID = ? and ACCEPT_M_ID = ?;";
		String sqlInsertNotify = "Insert into Tep101_Jome17.NOTIFY (TYPE, NOTIFICATION_BODY, BODY_STATUS, MEMBER_ID) "
								+ "values(2, ?, 3, ?);";
		int inserNewFriendResult = 0;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt1 = conn.prepareStatement(sqlInsertFriend);
			PreparedStatement pstmt2 = conn.prepareStatement(sqlseletctTableFriend);
			PreparedStatement pstmt3 = conn.prepareStatement(sqlInsertNotify);) {
			conn.setAutoCommit(false);
			try {
				pstmt1.setString(1, friendList.getInvite_M_ID());
				pstmt1.setString(2, friendList.getAccept_M_ID());
				int insertFriend = pstmt1.executeUpdate();
				int uid = -1;
				pstmt2.setString(1, friendList.getInvite_M_ID());
				pstmt2.setString(2, friendList.getAccept_M_ID());
				ResultSet rs = pstmt2.executeQuery();
				while(rs.next()) {
					uid = rs.getInt(1);
				}
				if(insertFriend < 1 || uid == -1) {
					System.out.println("insertFriend: " + insertFriend);
					System.out.println(pstmt1);
					System.out.println("selectUid : " + uid);
					System.out.println(pstmt2);
					throw new SQLException("Table Friend insert error! ");
				}
				pstmt3.setString(1, uid+ "");
				pstmt3.setString(2, friendList.getAccept_M_ID());
				int insertNotify = pstmt3.executeUpdate();
				if(insertNotify < 1) {
					throw new SQLException("Table Notify insert error! ");
				}
				conn.commit();
				inserNewFriendResult = 1;
			}catch (SQLException e) {
				conn.rollback();
				inserNewFriendResult = -1;
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inserNewFriendResult;
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
	
	
	public int changeNotiForFriendUpdate(FriendListBean bean) {
		int friendStatus = bean.getFriend_Status();
		String sqlNotify = "";
		if(friendStatus == 1) {
			sqlNotify = "delete from Tep101_Jome17.NOTIFY "
					+ "where TYPE = 2 "
					+ "and NOTIFICATION_BODY = ? "
					+ "and BODY_STATUS = 3;";
		}else if(friendStatus == 3) {
			sqlNotify = "Insert into Tep101_Jome17.NOTIFY (TYPE, NOTIFICATION_BODY, BODY_STATUS, MEMBER_ID) values(2, ?, 3, " + bean.getAccept_M_ID() + ");";
		}
		String sqlUpdateFriend = "update Tep101_Jome17.FRIEND_LIST "
								+ "SET INVITE_M_ID = ?, ACCEPT_M_ID = ?, FRIEND_STATUS = ? "
								+ "WHERE UID = ? ;";

		int updateFriendResult = 0;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt1 = conn.prepareStatement(sqlUpdateFriend);
			PreparedStatement pstmt2 = conn.prepareStatement(sqlNotify);) {
			conn.setAutoCommit(false);
			try {
				pstmt1.setString(1, bean.getInvite_M_ID());
				pstmt1.setString(2, bean.getAccept_M_ID());
				pstmt1.setInt(3, bean.getFriend_Status());
				pstmt1.setInt(4, bean.getuId());
				int updateResult = pstmt1.executeUpdate();
				if(updateResult < 1) {
					throw new SQLException("update FriendeList is error!");
				}
				pstmt2.setString(1, bean.getuId()+ "");
				int deleteResult = pstmt2.executeUpdate();
				if(deleteResult < 1) {
					throw new SQLException("delete notify is error!");
				}
				conn.commit();
				updateFriendResult = 1;
			}catch (SQLException e) {
				conn.rollback();
				updateFriendResult = -1;
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return updateFriendResult;
	}
	
	//查詢(查個人好友列表含名字)
	@Override
	public FriendListBean selectByKey(String keyword, String memberId) {
		String sql = "select " 
						+ "f.UID,"  
						+ "f.INVITE_M_ID,"
						+ "m.NICKNAME,"				//3
						+ "f.ACCEPT_M_ID,"
						+ "m1.NICKNAME," 
						+ "f.FRIEND_STATUS "		//6 
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
		String Id1 = checkList.getInvite_M_ID();
		String Id2 = checkList.getAccept_M_ID();
		String sql = "SELECT * "
					+ "FROM Tep101_Jome17.FRIEND_LIST "
					+ "WHERE "
						+ "INVITE_M_ID = ? and ACCEPT_M_ID = ? "
							+ "or "
						+ "INVITE_M_ID = ? and ACCEPT_M_ID = ?;";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, Id1);
			pstmt.setString(2, Id2);
			pstmt.setString(3, Id2);
			pstmt.setString(4, Id1);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				FriendListBean friendList = new FriendListBean();
				friendList.setuId(rs.getInt("UID"));
				friendList.setInvite_M_ID(rs.getString("INVITE_M_ID"));
				friendList.setAccept_M_ID(rs.getString("ACCEPT_M_ID"));
				friendList.setFriend_Status(rs.getInt("FRIEND_STATUS"));
				return friendList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<FriendListBean> selectAll(String memberId) {
		String sql = "select "
						+ "f.UID, "
						+ "f.INVITE_M_ID, "
						+ "m.NICKNAME, "			//3
						+ "f.ACCEPT_M_ID, "
						+ "m1.NICKNAME, "
						+ "f.FRIEND_STATUS "		//6
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
						+ "f.MODIFY_DATE desc; ";
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
					friends.add(friend);
				}
				return friends;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
	}
	
	@Override
	public int deletaByKey(String mId, String mId1) {
		String sqlGetTableId = "SELECT UID"
							+ " FROM Tep101_Jome17.FRIEND_LIST"
							+ " where "
								+ "INVITE_M_ID in(?, ?) "
								+ "and "
								+ "ACCEPT_M_ID in(?, ?);";
		String sqlRelation = "DELETE FROM Tep101_Jome17.FRIEND_LIST WHERE UID = ?;";
		String sqlNotify = "DELETE FROM Tep101_Jome17.NOTIFY "
							+ "WHERE TYPE = 2 and NOTIFICATION_BODY = ?;";
		int uid = -1;
		int deleteResult = -1;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt3 = conn.prepareStatement(sqlGetTableId);
			PreparedStatement pstmt1 = conn.prepareStatement(sqlRelation);
			PreparedStatement pstmt2 = conn.prepareStatement(sqlNotify);) {
			pstmt3.setString(1, mId);
			pstmt3.setString(2, mId1);
			pstmt3.setString(3, mId);
			pstmt3.setString(4, mId1);
			ResultSet rs = pstmt3.executeQuery();
			while(rs.next()) {
				uid = rs.getInt(1);
				System.out.println(pstmt3.toString());
			}
			conn.setAutoCommit(false);
			try{
				pstmt1.setInt(1, uid);
				int resultDelFriend = pstmt1.executeUpdate();
				if(resultDelFriend < 1) {
					throw new SQLException("table FRIEND_LIST is deleted in error!");
				}
				pstmt2.setString(1, uid+ "");
				int resultDelNotify = pstmt2.executeUpdate();
				if(resultDelNotify < 1) {
					throw new SQLException("table NOTIFY is deleted in error!");
				}
				conn.commit();
				deleteResult = 1;
			}catch(SQLException e) {
				conn.rollback();
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deleteResult;
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
	public List<FriendListBean> selectAllNoKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
