package web.jome17.jome_member.dao;

import java.math.BigDecimal;
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
//			System.out.println("# insert sql: " + pstmt.toString());
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
						+ "BE_RATED_ID = ? "
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

	@Override
	public ScoreBean selectByKey(String keyword, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ScoreBean> selectAll(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ScoreBean selectRelation(ScoreBean bean) {
		// TODO Auto-generated method stub
		return null;
	}
	

	
	/*
	 * 
	 * 暫無用方法
	 * 
	 */
	
	@Override
	public int update(ScoreBean bean) {
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
	public List<ScoreBean> selectAllNoKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
