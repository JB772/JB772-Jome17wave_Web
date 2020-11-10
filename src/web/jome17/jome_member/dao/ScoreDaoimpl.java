package web.jome17.jome_member.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import web.jome17.jome_member.bean.ScoreBean;
import web.jome17.jome_notify.bean.Notify;
import web.jome17.main.ServiceLocator;

public class ScoreDaoimpl implements CommonDao<ScoreBean, String> {
	
	public DataSource dataSource;
	
	

	public ScoreDaoimpl() {
		dataSource = ServiceLocator.getInstance().getDataSource();
	}

	@Override
	public int insert(ScoreBean bean) {
		String sql = "insert into Tep101_Jome17.SCORE(GROUP_ID, BE_RATED_ID, MEMBER_ID, RATING_SCORE) "
					+ "values (?,?,?,?)";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setObject(1, bean.getGroupId());
			pstmt.setObject(2, bean.getBeRatedId());
			pstmt.setObject(3, bean.getMemberId());
			pstmt.setObject(4, bean.getRatingScore());
			System.out.println("# insert sql: " + pstmt.toString());
			return pstmt.executeUpdate();	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	//各分數得到的次數
	@Override
	public String getCount(String memberId) {
		String sql ="select "
						+ "RATING_SCORE as SCORE, "
						+ "count(*) as count "
					+ "from "
						+ "Tep101_Jome17.SCORE "
					+ "where "
						+ "BE_RATED_ID = ? and RATING_SCORE in(1,2,3,4,5) "
					+ "group by "
						+ "RATING_SCORE;" ;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, memberId);
			ResultSet rs = pstmt.executeQuery();
			List<Map<String, String>> scoreCounts = new ArrayList<>();
			while(rs.next()) {
				Map<String, String> map = new HashMap<>();
				map.put(rs.getInt(1) +"", rs.getInt(2) +"");
				scoreCounts.add(map);
			}
			JsonObject jsObject = new JsonObject();
			jsObject.addProperty("scoreCounts", new Gson().toJson(scoreCounts));
			return jsObject.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//取得該groupId的所有評分資料
	@Override
	public List<ScoreBean> selectAll(String groupID) {
		String sql = "SELECT * "
					+ "FROM Tep101_Jome17.SCORE "
					+ "WHERE GROUP_ID = ?;";
	try(Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);) {
		pstmt.setString(1, groupID);
		ResultSet rs = pstmt.executeQuery();
		List<ScoreBean> scoreDatas = new ArrayList<ScoreBean>();
		while(rs.next()) {
			ScoreBean scoreData = new ScoreBean();
			scoreData.setScoreId(rs.getInt("SCORE_ID"));
			scoreData.setMemberId(rs.getString("MEMBER_ID"));
			scoreData.setBeRatedId(rs.getString("BE_RATED_ID"));
			scoreData.setGroupId(rs.getString("GROUP_ID"));
			scoreData.setRatingScore(rs.getInt("RATING_SCORE"));
			scoreDatas.add(scoreData);
		}
		return scoreDatas;	
	} catch (SQLException e) {
		e.printStackTrace();
	}
		return null;
	}
	
	//member進行評分
	@Override
	public int update(ScoreBean bean) {
		String sql = "";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return 0;
	}
	
	/*
	 * 做交易控制：做多筆score的update，並刪除先前的評分通知
	 */
	public int updateAndDelete(List<ScoreBean> ratingResults, Notify notify) {
		int successedCount = 0;
		
		String sqlUpdate = "UPDATE Tep101_Jome17.SCORE SET RATING_SCORE = ? WHERE SCORE_ID = ?;";
		String sqlDelete = "delete from Tep101_Jome17.NOTIFY where TYPE = ? and NOTIFICATION_BODY = ? and MEMBER_ID = ?;"; 
		try (
			Connection conn = dataSource.getConnection(); 
			PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate);
			PreparedStatement pstmtDelete = conn.prepareStatement(sqlDelete);
		){
			conn.setAutoCommit(false);
				try {
					for (ScoreBean aRated : ratingResults) {
						pstmtUpdate.setInt(1, aRated.getRatingScore());
						pstmtUpdate.setInt(2, aRated.getScoreId());
						successedCount += pstmtUpdate.executeUpdate();
						if (successedCount < (successedCount-1)) {
							throw new SQLException("Table score update is error: Update No." + successedCount);
						}
					}
					pstmtDelete.setInt(1, notify.getType());
					pstmtDelete.setString(2, notify.getNotificationBody());
					pstmtDelete.setString(3, notify.getMemberId());
					successedCount += pstmtDelete.executeUpdate();
					if (successedCount < (successedCount-1)) {
						throw new SQLException("Table notify delete is error!");
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
	
	/*
	 * 某個揪團某個人對大家的評分卷
	 */
	public List<ScoreBean> selectRatingList(String groupID, String memberId) {
		String sql = "SELECT s.*, m.NICKNAME " + "FROM Tep101_Jome17.SCORE s "
				+ "left join Tep101_Jome17.MEMBERINFO m on s.BE_RATED_ID = m.ID "
				+ "WHERE GROUP_ID = ? and MEMBER_ID = ?;";
		try (
				Connection conn = dataSource.getConnection(); 
				PreparedStatement pstmt = conn.prepareStatement(sql);
		){
			pstmt.setString(1, groupID);
			pstmt.setString(2, memberId);
			ResultSet rs = pstmt.executeQuery();
			List<ScoreBean> scoreDatas = new ArrayList<ScoreBean>();
			while (rs.next()) {
				ScoreBean scoreData = new ScoreBean();
				scoreData.setScoreId(rs.getInt("SCORE_ID"));
				scoreData.setMemberId(rs.getString("MEMBER_ID"));
				scoreData.setBeRatedId(rs.getString("BE_RATED_ID"));
				scoreData.setGroupId(rs.getString("GROUP_ID"));
				scoreData.setRatingScore(rs.getInt("RATING_SCORE"));
				scoreData.setBeRatedName(rs.getString("NICKNAME"));
				scoreDatas.add(scoreData);
			}
			return scoreDatas;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/*
	 * 
	 * 暫無用方法
	 * 
	 */

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
	public List<ScoreBean> selectAllNoKey() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ScoreBean selectRelation(ScoreBean bean) {

		return null;
	}

	@Override
	public ScoreBean selectByKey(String keyword, String key) {
		// TODO Auto-generated method stub
		return null;
	}
}
