package web.jome17.jome_member.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.sql.DataSource;

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
	
	//取得平均分
	@Override
	public String getCount(String memberId) {
		String sql ="select "
						+ "avg(RATING_SCORE), "
						+ "COUNT(*) "
					+ "from "
						+ "Tep101_Jome17.SCORE "
					+ "where "
						+ "BE_RATED_ID = ?;";			
		try (Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, memberId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String selectResult = "";
				Double d = rs.getDouble(1);
				BigDecimal bd = new BigDecimal(d);
				bd = bd.setScale(1, 4);
				selectResult =String.valueOf(bd)
								+ "/"
								+ String.valueOf(rs.getInt(2));
//								+ "次";
				return selectResult;
			}
		} catch (Exception e) {	
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
	public int deletaByKey(String key) {
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
