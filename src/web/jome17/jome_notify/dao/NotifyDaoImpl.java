package web.jome17.jome_notify.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import web.jome17.jome_group.Group;
import web.jome17.jome_member.bean.FriendListBean;
import web.jome17.jome_member.bean.ScoreBean;
import web.jome17.jome_notify.bean.AttenderBean;
import web.jome17.jome_notify.bean.Notify;
import web.jome17.main.ServiceLocator;

public class NotifyDaoImpl implements NotifyDao{
	DataSource  dataSource;
	

	public NotifyDaoImpl() {
		dataSource = ServiceLocator.getInstance().getDataSource();
	}


	/*
	 * 新增
	 */
	@Override
	public int insert(Notify bean) {
		int count = 0;
		String sql = "Insert into Tep101_Jome17.NOTIFY (TYPE, NOTIFICATION_BODY, BODY_STATUS, MEMBER_ID) values(?, ?, ?, ?);";
		try (
			Connection connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(sql);
		){
			pstmt.setInt(1, bean.getType());
			pstmt.setString(2, bean.getNotificationBody());
			pstmt.setInt(3, bean.getBodyStatus());
			pstmt.setString(4, bean.getMemberId());
			count = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	//交易控制於 回覆同意他人交友邀請
	public int insert2Noti(FriendListBean bean) {
		String sqlInsert1 = "Insert into Tep101_Jome17.NOTIFY "
							+ "(TYPE, NOTIFICATION_BODY, BODY_STATUS, MEMBER_ID)"
							+ " values(2, ?, 1, ?);";
		String sqlInsert2 = "Insert into Tep101_Jome17.NOTIFY "
							+ "(TYPE, NOTIFICATION_BODY, BODY_STATUS, MEMBER_ID)"
							+ " values(2, ?, 1, ?);";;
		int insertResult = -1;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt1 = conn.prepareStatement(sqlInsert1);
			PreparedStatement pstmt2 = conn.prepareStatement(sqlInsert2);) {
			conn.setAutoCommit(false);
			try {
				pstmt1.setInt(1, bean.getuId());
				pstmt1.setString(2, bean.getInvite_M_ID());
				int insertResult1 = pstmt1.executeUpdate();
				if(insertResult1 < 1) {
					throw new SQLException("Table notify insert1 is error!");
				}
				pstmt2.setInt(1, bean.getuId());
				pstmt2.setString(2, bean.getAccept_M_ID());
				int insertResult2 = pstmt2.executeUpdate();
				if(insertResult2 < 1) {
					throw new SQLException("Table notify insert2 is error!");
				}
				conn.commit();
				insertResult = 1;
			} catch (SQLException e) {
				conn.rollback();
				insertResult = -1;
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return insertResult;
	}
	
	/*
	 * 查詢(個人的)通知列表
	 */
	@Override
	public List<Notify> getAll(String memberId) {
		String sql = "select * from Tep101_Jome17.NOTIFY Where MEMBER_ID = ? order by BUILD_DATE desc;";
		List<Notify> notifiesList = new ArrayList<>();
		try (
			Connection connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(sql);
		){
			pstmt.setString(1, memberId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Notify notify = new Notify();
				notify.setNotificationId(rs.getInt(1));
				notify.setType(rs.getInt(2));
				notify.setNotificationBody(rs.getString(3));
				notify.setBodyStatus(rs.getInt(4));
				notify.setMemberId(rs.getString(5));
//System.out.println("notify: " + notify.getMemberId());
				notifiesList.add(notify);
			}
			return notifiesList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/*
	 * 刪除
	 */
	@Override
	public int delete(String notificationId) {
		int count = 0;
		String sql = "delete from Tep101_Jome17.NOTIFY where NOTIFICATION_ID = ?;";
		try (
			Connection connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(sql);
		){
			pstmt.setString(1, notificationId);
			count = pstmt.executeUpdate();
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return count;
	}
	
	@Override
	public int delete2(FriendListBean bean) {
		String sql = "delete from Tep101_Jome17.NOTIFY where TYPE = 2 and NOTIFICATION_BODY = ? and BODY_STATUS = 1;";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setInt(1, bean.getuId());
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	/*
	 * 找到要刪除的那筆資料
	 */
	@Override
	public int findNotificationId(Notify bean) {
		int notificationId = -1;
		String sql = "select notification_ID "
	 			+ "from Tep101_Jome17.NOTIFY "
	 			+ "where type = ? and NOTIFICATION_BODY = ? and BODY_STATUS = ? and MEMBER_ID = ?;";
	try (
		Connection connection = dataSource.getConnection();
		PreparedStatement pstmt = connection.prepareStatement(sql);
	){
		pstmt.setInt(1, bean.getType());
		pstmt.setString(2, bean.getNotificationBody());
		pstmt.setInt(3, bean.getBodyStatus());
		pstmt.setString(4, bean.getMemberId());
		ResultSet rs = pstmt.executeQuery();
		if (rs.next()) {
			notificationId = rs.getInt(1);
			return notificationId;
		}
	} catch (Exception e) {
		 e.printStackTrace();
	}
		return notificationId;
	}


	/*
	 * 用 UID 取得 FriendListBean 物件 (好友狀態紀錄)
	 */
	@Override
	public FriendListBean getFriendListBean(String notificationBody) {
		String sql =  "select f.UID, f.INVITE_M_ID, m.NICKNAME as INVITE_NAME, f.ACCEPT_M_ID, m2.NICKNAME as ACCEPT_NAME, f.FRIEND_STATUS, f.MODIFY_DATE "
		+ "from Tep101_Jome17.FRIEND_LIST f "
		+ "left join Tep101_Jome17.MEMBERINFO m on f.INVITE_M_ID = m.ID "
		+ "join Tep101_Jome17.MEMBERINFO m2 on f.ACCEPT_M_ID = m2.ID "
		+ "Where UID = ?;";
		try (
			Connection connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(sql);
		){
			pstmt.setString(1, notificationBody);
			ResultSet rs = pstmt.executeQuery();
			FriendListBean bean = new FriendListBean();
			if (rs.next()) {
				bean.setuId(rs.getInt(1));
				bean.setInvite_M_ID(rs.getString(2));
				bean.setInviteName(rs.getString(3));
				bean.setAccept_M_ID(rs.getString(4));
				bean.setAcceptName(rs.getString(5));
				bean.setFriend_Status(rs.getInt(6));
//				bean.setModify_Date(rs.getDate(5));
				return bean;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		String sql = "select * from Tep101_Jome17.FRIEND_LIST Where UID = ?;";
//		try (
//			Connection connection = dataSource.getConnection();
//			PreparedStatement pstmt = connection.prepareStatement(sql);
//		){
//			pstmt.setInt(1, notificationBody);
//			ResultSet rs = pstmt.executeQuery();
//			FriendListBean bean = new FriendListBean();
//			if (rs.next()) {
//				bean.setuId(rs.getInt(1));
//				bean.setInvite_M_ID(rs.getString(2));
//				bean.setInvite_M_ID(rs.getString(3));
//				bean.setFriend_Status(rs.getInt(4));
////				bean.setModify_Date(rs.getDate(5));
//			}
//
//			String sqlName = "select NICKNAME from Tep101_Jome17.MEMBERINFO Where MEMBER_ID = ?;";
//			try (
//				PreparedStatement psInvite = connection.prepareStatement(sqlName);
//				PreparedStatement psAccept = connection.prepareStatement(sqlName);
//			){
//				psInvite.setString(1, bean.getInvite_M_ID());
//				psAccept.setString(1, bean.getAccept_M_ID());
//				ResultSet rsInvite = psInvite.executeQuery();
//				ResultSet rsAccept = psAccept.executeQuery();
//				if (rsInvite.next()) {
//					bean.setInviteName(rsInvite.getString("NICKNAME"));
//				}
//				if (rsAccept.next()) {
//					bean.setAcceptName(rsAccept.getString("NICKNAME"));
//				}
//				return bean;
//			} catch (Exception e) {
//				 e.printStackTrace();
//			}
//		} catch (Exception e) {
//			 e.printStackTrace();
//		}
		return null;
	}
//	在資料庫貼好ID跟NAME的SQL語法。
//	String sql =  "select f.UID, f.INVITE_M_ID, m.NICKNAME as INVITE_NAME, f.ACCEPT_M_ID, m2.NICKNAME as ACCEPT_NAME, f.FRIEND_STATUS, f.MODIFY_DATE "
//				+ "from Tep101_Jome17.FRIEND_LIST f "
//				+ "left join Tep101_Jome17.MEMBERINFO m on f.INVITE_M_ID = m.ID "
//				+ "join Tep101_Jome17.MEMBERINFO m2 on f.ACCEPT_M_ID = m2.ID "
//				+ "Where UID = ?;";

	
	
	/*
	 * 用 Attender_No 取得 AttenderBean 物件 (單則揪團成功/失敗的Bean)
	 */
	@Override
	public AttenderBean getAttenderBean(String notificationBody) {
		String sql =  "SELECT g.GROUP_NAME, a.*, m.NICKNAME "
					+ "FROM Tep101_Jome17.ATTENDER a "
					+ "left join Tep101_Jome17.JOIN_GROUP g on g.GROUP_ID = a.GROUP_ID "
					+ "join Tep101_Jome17.MEMBERINFO m on a.MEMBER_ID = m.ID "
					+ "Where a.ATTENDER_NO = ?;";
			try (
			Connection connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(sql);
		){
			pstmt.setString (1, notificationBody);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				AttenderBean bean = new AttenderBean();
				bean.setGroupName(rs.getString(1));
				bean.setAttenderNo(rs.getInt(2));
				bean.setGroupId(rs.getString(3));
				bean.setAttendStatus(rs.getInt(4));
				bean.setModifyDate(rs.getString(5));
				bean.setRole(rs.getInt(6));
				bean.setMemberId(rs.getString(7));
				bean.setMemberName(rs.getString(8));
				return bean;
			}
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}

//	@Override  // 用GroupId與memberId找Attender資料
//	public AttenderBean getAttenderBean(int notificationBody, String memberId) {
//		String sql =  "SELECT g.GROUP_NAME, a.* "
//					+ "FROM Tep101_Jome17.JOIN_GROUP g "
//					+ "left join Tep101_Jome17.ATTENDER a on g.GROUP_ID = a.GROUP_ID "
//					+ "Where a.GROUP_ID = ? and a.ROLE = 2 and a.MEMBER_ID = ?;";
//		try (
//			Connection connection = dataSource.getConnection();
//			PreparedStatement pstmt = connection.prepareStatement(sql);
//		){
//			pstmt.setInt(1, notificationBody);
//			pstmt.setString(2, memberId);
//			ResultSet rs = pstmt.executeQuery();
//			AttenderBean bean = new AttenderBean();
//			bean.setGroupName(rs.getString(1));
//			bean.setAttenderNo(rs.getInt(2));
//			bean.setGroupId(rs.getInt(3));
//			bean.setAttendStatus(rs.getInt(4));
//			bean.setModifyDate(rs.getString(5));
//			bean.setRole(rs.getInt(6));
//			bean.setMemberId(rs.getString(7));
//			return bean;
//		} catch (Exception e) {
//			 e.printStackTrace();
//		}
//		return null;
//	}


	/*
	 * 用 Group_ID 與 memberId 取得 ScoreList  (評分列表)
	 */
	@Override
	public List<ScoreBean> getScoreList(String notificationBody, String memberId) {
		 String sql = "select s.*, g.GROUP_NAME "
		 			+ "from Tep101_Jome17.SCORE s l"
		 			+ "eft join Tep101_Jome17.JOIN_GROUP g on g.GROUP_ID = s.GROUP_ID "
		 			+ "WHERE s.GROUP_ID = ? and s.MEMBER_ID = ?;";
		 List<ScoreBean> scoreList = new ArrayList<>();
		 try (
			Connection connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(sql);
		){
			pstmt.setString(1, notificationBody);
			pstmt.setString(2, memberId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				ScoreBean bean = new ScoreBean();
				bean.setScoreId(rs.getInt(1));
				bean.setMemberId(rs.getString(2));
				bean.setBeRatedId(rs.getString(3));
				bean.setRatingScore(rs.getInt(4));
				bean.setGroupId(rs.getString(5));
				bean.setGroupName(rs.getString(7));
				scoreList.add(bean);
			}
			return scoreList;
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}
	
	
	

	/*
//	 * 取得 GroupBean 物件 (單則揪團資料)
	 */
	@Override
	public Group getGroupBean(String notificationBody) {

		String sql = "SELECT * FROM Tep101_Jome17.JOIN_GROUP Where GROUP_ID = ?;";
		try (
				Connection connection = dataSource.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sql);
			){
				pstmt.setString(1, notificationBody);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					Group bean = new Group();
					bean.setGrouptId(rs.getString(1));
					bean.setGroupName(rs.getString(2));
					return bean;
				}
			} catch (Exception e) {
				 e.printStackTrace();
			}
		return null;
	}
	
	/*
	 * 做交易控制：做多筆notify的insert，用於評分通知(type 3)
	 */
	public int insertNotiForRating(List<Notify> ratingNotifies) {
		int successedCount = 0;
		String sql = "Insert into Tep101_Jome17.NOTIFY (TYPE, NOTIFICATION_BODY, MEMBER_ID) values(3, ?, ?);";
		try (
			Connection conn = dataSource.getConnection(); 
			PreparedStatement pstmt = conn.prepareStatement(sql);
		){
			conn.setAutoCommit(false);
				try {
					for (Notify notify : ratingNotifies) {
						pstmt.setString(1, notify.getNotificationBody());
						pstmt.setString(2, notify.getMemberId());
						successedCount += pstmt.executeUpdate();
						if (successedCount < (successedCount-1)) {
							throw new SQLException("Table notify insert is error: Insert No." + successedCount);
						}
					}
					conn.commit();
				}catch (SQLException e) {
					conn.rollback();
					successedCount = -1;
					e.printStackTrace();
				}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return successedCount;
	}


	@Override
	public int insertForFriendUpdate(FriendListBean bean) {
		// TODO Auto-generated method stub
		return 0;
	}












	




}
