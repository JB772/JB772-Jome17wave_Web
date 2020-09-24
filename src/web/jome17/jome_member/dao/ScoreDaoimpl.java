package web.jome17.jome_member.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.sql.DataSource;

import web.jome17.jome_member.bean.ScoreBean;
import web.jome17.main.ServiceLocator;

public class ScoreDaoimpl implements CommonDao<ScoreBean, String> {
	
	public DataSource dataSource;
	
	

	public ScoreDaoimpl(DataSource dataSource) {
		dataSource = ServiceLocator.getInstance().getDataSource();
	}

	@Override
	public int insert(ScoreBean bean) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ScoreBean selectByKey(String keyword, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScoreBean selectRelation(ScoreBean bean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ScoreBean> selectAll(String key) {
		// TODO Auto-generated method stub
		return null;
	}

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
	public ScoreBean login(String account, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getImage(String acconut) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCount(String memberId) {
		String sql = "select"
				+ "avg(RATING_SCORE)"				
			+ "from"
				+ "Tep101_Jome17.SCORE"	
			+ "where"
				+ "? = BE_RATED_ID;";			
		try (Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, memberId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String selectResult = "";
				selectResult = String.valueOf(rs.getObject(1));
				return selectResult;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
