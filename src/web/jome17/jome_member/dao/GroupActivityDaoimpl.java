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

public class GroupActivityDaoimpl implements CommonDao<PersonalGroup, String>{
	
	private DataSource dataSource;
	public GroupActivityDaoimpl() {
		dataSource = ServiceLocator.getInstance().getDataSource();
	}
	//新增揪團活動；交易控制：同時新增團員名單；
	@Override
	public int insert(PersonalGroup bean) {
		String sqlJoinGroup = "insert into Tep101_Jome17.JOIN_GROUP "
						+ "(Group_ID, GROUP_NAME, ASSEMBLE_TIME,"			//3
						+ " GROUP_END_TIME, SIGN_UP_END, SURF_POINT_ID,"	//6
						+ " GROUP_LIMIT, GENDER, NOTICE,"					//9
						+ " GROUP_STATUS, IMAGE) "							//11
					+ "values "
						+ "(?, ?, ?, "		//3
						+ " ?, ?, ?, "		//6
						+ "	?, ?, ?, "		//9
						+ " ?, ?) ;";		//11
		String sqlAttender = "insert into Tep101_Jome17.ATTENDER "
						+ "(Group_ID, ATTEDN_STATUS,"		//2
						+ " ROLE, MEMBER_ID) "				//4
					+ "values "
						+ "(?, ?,"		//2
						+ " ?, ?) ;";	//4
		int insertResult =  0;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt1 = conn.prepareStatement(sqlJoinGroup);
			PreparedStatement pstmt2 = conn.prepareStatement(sqlAttender);) {
			conn.setAutoCommit(false);
			try {
				//新增group
				pstmt1.setString(1, bean.getGroupId());
				pstmt1.setString(2, bean.getGroupName());
				pstmt1.setString(3, bean.getAssembleTime());	//3
				pstmt1.setString(4, bean.getGroupEndTime());
				pstmt1.setString(5, bean.getSignUpEnd());
				pstmt1.setInt(6, bean.getSurfPointId());		//6
				pstmt1.setInt(7, bean.getGroupLimit());
				pstmt1.setInt(8, bean.getGender());
				pstmt1.setString(9, bean.getNotice());			//9
				pstmt1.setInt(10, 1);
				pstmt1.setBytes(11, bean.getgImage());			//11
				int resultInsertGroup = pstmt1.executeUpdate();
				if(resultInsertGroup < 1) {
					throw new SQLException("insert JOIN_GROUP error!");
				}
				//新增attender
				pstmt2.setString(1, bean.getGroupId());
				pstmt2.setInt(2, bean.getAttenderStatus());
				pstmt2.setInt(3, bean.getRole());
				pstmt2.setString(4, bean.getMemberId());
				int resultInserAttender = pstmt2.executeUpdate();
				if(resultInserAttender < 1) {
					throw new SQLException("insert ATTENDER error!");
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

	//更新揪團的資料
	@Override
	public int update(PersonalGroup bean) {
		byte[] groupImage = bean.getgImage();
		String sql = "";
		String sqlGroup = "" ;
		String sqlAttender = "";
		int updateResult = 0;
		if (groupImage == null) {
			if(bean.getAttenderStatus() != 0) {
				//團長更新資料
				sql = "update Tep101_Jome17.JOIN_GROUP set "
						+ " GROUP_NAME = ?, ASSEMBLE_TIME = ?,"			//2		
						+ " GROUP_END_TIME = ?, SIGN_UP_END = ?,"		//4
						+ " SURF_POINT_ID = ?, GROUP_LIMIT = ?,"		//6
						+ " GENDER = ?, NOTICE = ?,"					//8
						+ " GROUP_STATUS = ?"							//9
					+ " where "
						+ "GROUP_ID = ?";								//10
			}else {
				//團長退團
					sqlGroup = "update Tep101_Jome17.JOIN_GROUP set "
									+ "GROUP_STATUS = 0"
								+ " where "
									+ "GROUP_ID = ?";
					sqlAttender = "update Tep101_Jome17.ATTENDER set "
									+ "ATTEND_STATUS = 2"
								+ " where "
									+ "GROUP_ID = ?";
			}		
		}else {
			//團長改圖
			sql = "update Tep101_Jome17.JOIN_GROUP set "
					+ " GROUP_NAME = ?, ASSEMBLE_TIME = ?,"			//2		
					+ " GROUP_END_TIME = ?, SIGN_UP_END = ?,"		//4
					+ " SURF_POINT_ID = ?, GROUP_LIMIT = ?,"		//6
					+ " GENDER = ?, NOTICE = ?,"					//8
					+ " GROUP_STATUS = ?, IMAGE = ?"				//10								
				+ " where "
					+ "GROUP_ID = ?";								//11					
		}
		if(bean.getAttenderStatus() != 0) {
			try(Connection conn = dataSource.getConnection();
					PreparedStatement pstmt = conn.prepareStatement(sql);) {
					pstmt.setString(1, bean.getGroupName());
					pstmt.setString(2, bean.getAssembleTime());
					pstmt.setString(3, bean.getGroupEndTime());
					pstmt.setString(4, bean.getSignUpEnd());
					pstmt.setInt(5, bean.getSurfPointId());
					pstmt.setInt(6, bean.getGroupLimit());
					pstmt.setInt(7, bean.getGender());
					pstmt.setString(8, bean.getNotice());
					pstmt.setInt(9, bean.getGroupStatus());
					if(groupImage == null) {
						pstmt.setString(10, bean.getGroupId());
					}else {
						pstmt.setBytes(10, groupImage);
						pstmt.setString(11, bean.getGroupId());
					}
					return pstmt.executeUpdate();
				} catch (Exception e) {
					e.printStackTrace();
					return -1;
				}
		}else {
			try(Connection conn = dataSource.getConnection();
				PreparedStatement pstmt1 = conn.prepareStatement(sqlGroup);
				PreparedStatement pstmt2 = conn.prepareStatement(sqlAttender);) {
				conn.setAutoCommit(false);
					try {
						pstmt1.setString(1, bean.getGroupId());
						int resultUpdateGroup = pstmt1.executeUpdate();
						if(resultUpdateGroup < 1) {
							throw new SQLException("table JOIN_GROUP is update in error!");
						}
						pstmt2.setString(1, bean.getGroupId());
						int resultUpdateAttender = pstmt2.executeUpdate();
						if(resultUpdateAttender < 1) {
							throw new SQLException("table ATTENDER is update in error!");
						}
					} catch (SQLException e) {
						conn.rollback();
						updateResult = -1;
						e.printStackTrace();
					}
					conn.commit();
					updateResult = 1;
					return updateResult;
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		}
	}

	//主揪按下退出揪團的按鈕就是 刪除該groupId的揪團資料；交易控制：一併刪除ATTENDER表的該groupId資料。
	@Override
	public int deletaByKey(String groupId, String key) {
		String sqlDelGroup = "DELETE from Tep101_Jome17.JOIN_GROUP where GROUP_ID = ? ;";
		String sqlDelAttender = "DELETE from Tep101_Jome17.ATTENDER where GROUP_ID = ? ;";
		int deleteResult = 0;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt1 = conn.prepareStatement(sqlDelGroup);
			PreparedStatement pstmt2 = conn.prepareStatement(sqlDelAttender);) {
			conn.setAutoCommit(false);
			try {
				pstmt1.setString(1, groupId);
				int resultDelGroup = pstmt1.executeUpdate();
				if(resultDelGroup < 1) {
					throw new SQLException("table JOIN_GROUP is deleted in error!");
				}
				pstmt2.setString(1, groupId);
				int resultDelAttender = pstmt2.executeUpdate();
				if(resultDelAttender < 1) {
					throw new SQLException("table ATTENDER is deleted in error!");
				}
				conn.commit();
				deleteResult = 1;
			}catch(SQLException e) {
				conn.rollback();
				deleteResult = -1;
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deleteResult;
	}

	//拿揪團的圖
	@Override
	public byte[] getImage(String groupId) {
		String sql = "select IMAGE from Tep101_Jome17.JOIN_GROUP where GROUP_ID = ? ;";
		byte[] groupImage = null;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, groupId);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				groupImage = rs.getBytes("IMAGE");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groupImage;
	}
	
	//拿到主揪的次數 及 參加揪團的次數
	@Override
	public String getCount(String groupId) {
		String sql = "select count(*) "
					+ "from Tep101_Jome17.ATTENDER "
					+ "where "
					+ "GROUP_ID = ? and ATTEDN_STATUS = 1;";
		String resultCount = 0 + "";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, groupId);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				resultCount = rs.getInt(1) + "";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultCount;
	}
	
	//直接拿到所有揪團資料(含主揪人mId及nickname)
	@Override
	public List<PersonalGroup> selectAllNoKey() {
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
				+ "j.SIGN_UP_END, j.GROUP_LIMIT, "	//13
				+ "j.NOTICE, j.GROUP_STATUS "		//15
			+ "from "
				+ "Tep101_Jome17.ATTENDER a "
					+ "left join Tep101_Jome17.JOIN_GROUP j "
					+ "on j.GROUP_ID = a.GROUP_ID "
						+ "left join Tep101_Jome17.MEMBERINFO m "
						+ "on m.ID = a.MEMBER_ID "
							+ "left join Tep101_Jome17.SURF_POINT s "
							+ "on j.SURF_POINT_ID = s.SURF_POINT_ID "
					+ "where "
						+ "a.ROLE = 1 "
//						+ " and j.GROUP_LIMIT <= ( "
//						+ " select count(*) from Tep101_Jome17.ATTENDER "
//						+ " where GROUP_ID = ? "
//						+ " and ATTEDN_STATUS = 1 ) "
					+ "order by "
						+ "j.MODIFY_TIME "
						+ "desc;";
		String sqlJoinCountNow = "select "
									+ "count(*) "
								+ "from "
									+ "Tep101_Jome17.ATTENDER "
								+ "where "
									+ "GROUP_ID = ? and ATTEDN_STATUS = 1;" ;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			ResultSet rs = pstmt.executeQuery();
			List<PersonalGroup> pGroups = new ArrayList<PersonalGroup>();
			List<PersonalGroup> pCountGroups = new ArrayList<PersonalGroup>();
			while(rs.next()) {
				PersonalGroup pGroup = new PersonalGroup();
				pGroup.setMemberId(rs.getString("MEMBER_ID"));
				pGroup.setNickname(rs.getString("NICKNAME"));
				pGroup.setMemberGender(rs.getInt(3));						//3
				pGroup.setAttenderId(rs.getInt("ATTENDER_NO"));
				pGroup.setAttenderStatus(rs.getInt("ATTEDN_STATUS"));
				pGroup.setRole(rs.getInt("ROLE"));							//6
				pGroup.setSurfName(rs.getString("SURF_NAME"));
				pGroup.setGroupId(rs.getString("GROUP_ID"));
				pGroup.setGroupName(rs.getString("GROUP_NAME"));			//9
				pGroup.setAssembleTime(rs.getString("ASSEMBLE_TIME"));
				pGroup.setGroupEndTime(rs.getString("GROUP_END_TIME"));
				pGroup.setSignUpEnd(rs.getString("SIGN_UP_END"));			//12
				pGroup.setNotice(rs.getString("NOTICE"));
				pGroup.setGroupLimit(rs.getInt("GROUP_LIMIT"));
				pGroup.setGroupStatus(rs.getInt("GROUP_STATUS"));			//15
				pGroups.add(pGroup);
			}
//			for(PersonalGroup pGroup : pGroups) {
//				PreparedStatement pstmt2 = conn.prepareStatement(sqlJoinCountNow);
//				pstmt2.setString(1, pGroup.getMemberId());
//				ResultSet rs2 = pstmt2.executeQuery();
//				while(rs.next()) {
//					pGroup.setJoinCountNow(rs2.getInt(1));
//					pCountGroups.add(pGroup);
//				}
//			}
//			pGroups.clear();
//			pGroups = null;
//			return pCountGroups;
			return pGroups;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	//搜尋某GroupId的記錄，含所有attender
	@Override
	public List<PersonalGroup> selectAll(String groupId) {
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
							+ "on j.SURF_POINT_ID = s.SURF_POINT_ID "
			+ "where "
				+ "j.GROUP_ID = ? "
			+ "order by "
				+ "a.MODIFY_TIME desc;";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, groupId);
			ResultSet rs = pstmt.executeQuery();
			List<PersonalGroup> pGroups = new ArrayList<PersonalGroup>();
			while(rs.next()) {
				PersonalGroup pGroup = new PersonalGroup();
				pGroup.setMemberId(rs.getString("MEMBER_ID"));
				pGroup.setNickname(rs.getString("NICKNAME"));
				pGroup.setMemberGender(rs.getInt(3));						//3
				pGroup.setAttenderId(rs.getInt("ATTENDER_NO"));
				pGroup.setAttenderStatus(rs.getInt("ATTEDN_STATUS"));
				pGroup.setRole(rs.getInt("ROLE"));							//6
				pGroup.setSurfName(rs.getString("SURF_NAME"));
				pGroup.setGroupId(rs.getString("GROUP_ID"));
				pGroup.setGroupName(rs.getString("GROUP_NAME"));			//9
				pGroup.setAssembleTime(rs.getString("ASSEMBLE_TIME"));
				pGroup.setGroupEndTime(rs.getString("GROUP_END_TIME"));
				pGroup.setSignUpEnd(rs.getString("SIGN_UP_END"));			//12
				pGroup.setGroupLimit(rs.getInt("GROUP_LIMIT"));
//				pGroup.setGender(rs.getInt("GENDER"));
				pGroup.setNotice(rs.getString("NOTICE"));
				pGroup.setGroupStatus(rs.getInt("GROUP_STATUS"));			//15
				pGroups.add(pGroup);
			}
			return pGroups;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	//搜尋某一場揪團的所有資料(含主揪、浪點)
	@Override
	public PersonalGroup selectByKey(String memberId, String groupId) {
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
				+ "j.GROUP_ID = ? "
				+ "and a.MEMBER_ID = ?;";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, groupId);
			pstmt.setString(2, memberId);
			ResultSet rs = pstmt.executeQuery();
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
//			pGroup.setGender(rs.getInt("GENDER"));
			pGroup.setGroupStatus(rs.getInt("GROUP_STATUS"));			//15
			return pGroup;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * 暫時用不到的方法
	 */
	
	@Override
	public PersonalGroup selectRelation(PersonalGroup bean) {
		// TODO Auto-generated method stub
		return null;
	}

}
