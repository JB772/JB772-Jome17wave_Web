package web.jome17.jome_member.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import web.jome17.jome_member.bean.PersonalGroup;
import web.jome17.main.ServiceLocator;

public class PerGroupDaoimpl implements CommonDao<PersonalGroup, String>{
	public DataSource dataSource;

	public PerGroupDaoimpl() {
		dataSource = ServiceLocator.getInstance().getDataSource();
	}
	
	//新增Attender表
	@Override
	public int insert(PersonalGroup bean) {
		String sql = "insert into Tep101_Jome17.ATTENDER "
						+ "(Group_ID, ATTEND_STATUS, ROLE, MEMBER_ID) "
					+ "values "
						+ "(?,?,?,?) ;";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, bean.getGroupId());
			pstmt.setInt(2, bean.getAttenderStatus());
			pstmt.setInt(3, bean.getRole());
			pstmt.setString(4, bean.getMemberId());
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	//修改Attender表
	@Override
	public int update(PersonalGroup bean) {
		String sql = "update Tep101_Jome17.ATTENDER set "
						+ "ATTEND_STATUS = ?, "
						+ "ROLE = ? "
					+"where "
						+ "ATTENDER_NO = ?";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setInt(1, bean.getAttenderStatus());
			pstmt.setInt(2, bean.getRole());
			pstmt.setInt(3, bean.getAttenderId());
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return -1;
	}
	
	//取得memberId 其ATTEND_STATUS = 1 的次數
	@Override
	public String getCount(String memberId) {
		String noCount = "0";
		String sql = "select "
						+ "count(*) "
					+ "from "
						+ "Tep101_Jome17.ATTENDER "
					+ "where "
						+ "MEMBER_ID = ? "
							+ "and "
						+ "ATTEDN_STATUS = 1;";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, memberId);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				return String.valueOf(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return noCount;
	}
	
	//用Attender的某欄位 查詢 某一筆Attender left join Group
	@Override
	public PersonalGroup selectByKey(String keyword, String key) {
		String sql = "select "
						+ "a.MEMBER_ID, "
						+ "a.ATTENDER_NO, "
						+ "a.ATTEDN_STATUS, "
						+ "a.ROLE, "
						+ "j.* "
					+ "from "
						+ "Tep101_Jome17.ATTENDER a "
						+ "left join Tep101_Jome17.JOIN_GROUP j "
							+ "on j.GROUP_ID = a.GROUP_ID "
					+ "where "
						+ "a.? = ? ;";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, keyword);
			pstmt.setString(2, key);
			ResultSet rs = pstmt.executeQuery();
			PersonalGroup perGroup = new PersonalGroup();
			if(rs.next()) {
				perGroup.setMemberId(rs.getString("MEMBER_ID"));
				perGroup.setAttenderId(rs.getInt("ATTENDER_NO"));
				perGroup.setAttenderStatus(rs.getInt("ATTEND_STATUS"));
				perGroup.setRole(rs.getInt("ROLE"));
				perGroup.setGroupId(rs.getString("GROUP_ID"));
				perGroup.setGroupName(rs.getString("GROUP_NAME"));
				perGroup.setAssembleTime(rs.getString("ASSEMBLE_TIME"));
				perGroup.setGroupEndTime(rs.getString("GROUP_END_TIME"));
				perGroup.setSignUpEnd(rs.getString("SIGN_UP_END"));
				perGroup.setSurfPointId(rs.getInt("SURF_POINT_ID"));
				perGroup.setGroupLimit(rs.getInt("GROUP_LIMIT"));
				perGroup.setGender(rs.getInt("GENDER"));
				perGroup.setGroupStatus(rs.getInt("GROUP_STATUS"));
			}
			return perGroup;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//查詢memberId的所有Attender left join Group
	@Override
	public List<PersonalGroup> selectAll(String mId) {
		String sql = "select "
						+ "a.MEMBER_ID, "
						+ "a.ATTENDER_NO, "
						+ "a.ATTEDN_STATUS, "
						+ "a.ROLE, "
						+ "j.* "
					+ "from "
						+ "Tep101_Jome17.ATTENDER a "
						+ "left join Tep101_Jome17.JOIN_GROUP j "
							+ "on j.GROUP_ID = a.GROUP_ID "
					+ "where "
						+ "a.MEMBER_ID = ? "
					+ "order by "
						+ "j.GROUP_END_TIME desc;";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, mId);
			ResultSet rs = pstmt.executeQuery();
			List<PersonalGroup> personalGroups = new ArrayList<PersonalGroup>();
			while(rs.next()) {
				PersonalGroup perGroup = new PersonalGroup();
				perGroup.setMemberId(rs.getString("MEMBER_ID"));
				perGroup.setAttenderId(rs.getInt("ATTENDER_NO"));
				perGroup.setAttenderStatus(rs.getInt("ATTEND_STATUS"));
				perGroup.setRole(rs.getInt("ROLE"));
				perGroup.setGroupId(rs.getString("GROUP_ID"));
				perGroup.setGroupName(rs.getString("GROUP_NAME"));
				perGroup.setAssembleTime(rs.getString("ASSEMBLE_TIME"));
				perGroup.setGroupEndTime(rs.getString("GROUP_END_TIME"));
				perGroup.setSignUpEnd(rs.getString("SIGN_UP_END"));
				perGroup.setSurfPointId(rs.getInt("SURF_POINT_ID"));
				perGroup.setGroupLimit(rs.getInt("GROUP_LIMIT"));
				perGroup.setGender(rs.getInt("GENDER"));
				perGroup.setGroupStatus(rs.getInt("GROUP_STATUS"));
				personalGroups.add(perGroup);
			}
			return personalGroups;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * 暫時無用到方法
	 */
	
	@Override
	public byte[] getImage(String acconut) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PersonalGroup selectRelation(PersonalGroup bean) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PersonalGroup login(String account, String password) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int deletaByKey(String key) {
		// TODO Auto-generated method stub
		return 0;
	}
}
