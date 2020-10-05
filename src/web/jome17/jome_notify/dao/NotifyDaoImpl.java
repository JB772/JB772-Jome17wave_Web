package web.jome17.jome_notify.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.sun.org.apache.regexp.internal.recompile;
import com.sun.swing.internal.plaf.basic.resources.basic;

import web.jome17.jome_group.Group;
import web.jome17.jome_member.bean.FriendListBean;
import web.jome17.jome_member.bean.ScoreBean;
import web.jome17.jome_member.dao.CommonDao;
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
		String sql = "Insert into Tep101_Jome17.NOTIFY (TYPE, NOTIFICATION_BODY, MEMBER_ID) values(?, ?, ?);";
		try (
			Connection connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(sql);
		){
			pstmt.setInt(1, bean.getType());
			pstmt.setInt(2, bean.getNotificationBody());
			pstmt.setString(3, bean.getMemberId());
			count = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
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
				notify.setNotificationBody(rs.getInt(3));
				notify.setMemberId(rs.getString(4));
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
	public int deletaByKey(String buildDate) {
		// TODO Auto-generated method stub
		return 0;
	}


	/*
	 * 用 UID 取得 FriendListBean 物件 (好友狀態紀錄)
	 */
	@Override
	public FriendListBean getFriendListBean(int notificationBody) {
		String sql = "select * from Tep101_Jome17.FRIEND_LIST Where UID = ?;";
		try (
			Connection connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(sql);
		){
			pstmt.setInt(1, notificationBody);
			ResultSet rs = pstmt.executeQuery();
			FriendListBean bean = new FriendListBean();
			if (rs.next()) {
				bean.setuId(rs.getInt(1));
				bean.setInvite_M_ID(rs.getString(2));
				bean.setInvite_M_ID(rs.getString(3));
				bean.setFriend_Status(rs.getInt(4));
//				bean.setModify_Date(rs.getDate(5));
			}

			String sqlName = "select NICKNAME from Tep101_Jome17.MEMBER_INFO Where MEMBER_ID = ?;";
			try (
				PreparedStatement psInvite = connection.prepareStatement(sqlName);
				PreparedStatement psAccept = connection.prepareStatement(sqlName);
			){
				psInvite.setString(1, bean.getInvite_M_ID());
				psAccept.setString(1, bean.getAccept_M_ID());
				ResultSet rsInvite = psInvite.executeQuery();
				ResultSet rsAccept = psAccept.executeQuery();
				if (rsInvite.next()) {
					bean.setInviteName(rsInvite.getString("NICKNAME"));
				}
				if (rsAccept.next()) {
					bean.setAcceptName(rsAccept.getString("NICKNAME"));
				}
				return bean;
			} catch (Exception e) {
				 e.printStackTrace();
			}
		} catch (Exception e) {
			 e.printStackTrace();
		}
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
	public AttenderBean getAttenderBean(int notificationBody) {
		String sql =  "SELECT g.GROUP_NAME, a.* "
				+ "FROM Tep101_Jome17.JOIN_GROUP g "
				+ "left join Tep101_Jome17.ATTENDER a on g.GROUP_ID = a.GROUP_ID "
				+ "Where a.ATTENDER_NO = ?;";
			try (
			Connection connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(sql);
		){
			pstmt.setInt(1, notificationBody);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				AttenderBean bean = new AttenderBean();
				bean.setGroupName(rs.getString(1));
				bean.setAttenderNo(rs.getInt(2));
				bean.setGroupId(rs.getInt(3));
				bean.setAttendStatus(rs.getInt(4));
				bean.setModifyDate(rs.getString(5));
				bean.setRole(rs.getInt(6));
				bean.setMemberId(rs.getString(7));
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
	public List<ScoreBean> getScoreList(int notificationBody, String memberId) {
		 String sql = "select s.*, g.GROUP_NAME "
		 			+ "from Tep101_Jome17.SCORE s l"
		 			+ "eft join Tep101_Jome17.JOIN_GROUP g on g.GROUP_ID = s.GROUP_ID "
		 			+ "WHERE s.GROUP_ID = ? and s.MEMBER_ID = ?;";
		 List<ScoreBean> scoreList = new ArrayList<>();
		 try (
			Connection connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(sql);
		){
			pstmt.setInt(1, notificationBody);
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
	public Group getGroupBean(int notificationBody) {
		String sql = "";
//		????
		return null;
	}









	




}
