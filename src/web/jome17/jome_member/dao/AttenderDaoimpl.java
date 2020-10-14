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

public class AttenderDaoimpl implements CommonDao<PersonalGroup, String>{
	public DataSource dataSource;

	public AttenderDaoimpl() {
		dataSource = ServiceLocator.getInstance().getDataSource();
	}
	
	//新增Attender表；增加成員
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
	
	//修改Attender表；團員變更參加狀態
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
						+ " a.? = ? ;";	//a.MEMBER_ID  or a.GROUP_ID
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
	
	//查詢memberId的所有揪團記錄 left join Group
	@Override
	public List<PersonalGroup> selectAll(String mId) {
		String sql = "select "
				+ "a.MEMBER_ID, "
				+ "m.NICKNAME, "
				+ "m.GENDER, "			//3
				+ "a.ATTENDER_NO, "
				+ "a.ATTEDN_STATUS, "	
				+ "a.ROLE, "			//6
				+ "s.SURF_NAME, "
				+ "j.GROUP_ID, j.GROUP_NAME, "		//9
				+ "j.ASSEMBLE_TIME, j.GROUP_END_TIME, "		//11
				+ "j.SIGN_UP_END, j.GROUP_LIMIT, "			//13
				+ "j.NOTICE, j.GROUP_STATUS "				//15
			+ "from "
				+ "Tep101_Jome17.ATTENDER a "
					+ "left join Tep101_Jome17.JOIN_GROUP j "
					+ "on j.GROUP_ID = a.GROUP_ID "
						+ "left join Tep101_Jome17.MEMBERINFO m "
						+ "on m.ID = a.MEMBER_ID "
							+ "left join Tep101_Jome17.SURF_POINT s "
							+ "on j.SURF_POINT_ID = s.SURF_POINT_ID"
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
				PersonalGroup pGroup = new PersonalGroup();
				pGroup.setMemberId(rs.getString("MEMBER_ID"));
				pGroup.setNickname(rs.getString("NICKNAME"));
				pGroup.setMemberGender(rs.getInt(3));						//3
				pGroup.setAttenderId(rs.getInt("ATTENDER_NO"));
				pGroup.setAttenderStatus(rs.getInt("ATTEND_STATUS"));
				pGroup.setRole(rs.getInt("ROLE"));							//6
				pGroup.setSurfName(rs.getString("SURF_NAME"));
				pGroup.setGroupId(rs.getString("GROUP_ID"));
				pGroup.setGroupName(rs.getString("GROUP_NAME"));			//9
				pGroup.setAssembleTime(rs.getString("ASSEMBLE_TIME"));
				pGroup.setGroupEndTime(rs.getString("GROUP_END_TIME"));
				pGroup.setSignUpEnd(rs.getString("SIGN_UP_END"));			//12
				pGroup.setSurfPointId(rs.getInt("SURF_POINT_ID"));
				pGroup.setGroupLimit(rs.getInt("GROUP_LIMIT"));
//				pGroup.setGender(rs.getInt("GENDER"));
				pGroup.setGroupStatus(rs.getInt("GROUP_STATUS"));			//15

				personalGroups.add(pGroup);
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
	public int deletaByKey(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<PersonalGroup> selectAllNoKey() {
		// TODO Auto-generated method stub
		return null;
	}
}