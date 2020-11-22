package web.jome17.jome_member.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import web.jome17.jome_member.bean.PersonalGroup;
import web.jome17.jome_notify.bean.AttenderBean;
import web.jome17.main.ServiceLocator;

public class AttenderDaoimpl implements CommonDao<PersonalGroup, String>{
	public DataSource dataSource;

	public AttenderDaoimpl() {
		dataSource = ServiceLocator.getInstance().getDataSource();
	}
	

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
	
	/*
	 * 新增Attender表；增加成員(本人)，增加notify(團長)
	 */
	public int insertAtenderBean (AttenderBean bean, String groupHeadId) {
		String sqlAttenderInsert =  "insert into Tep101_Jome17.ATTENDER "
									+ "(Group_ID, ATTEDN_STATUS, ROLE, MEMBER_ID) "
								+ "values "
									+ "(?,?,?,?) ;";
		String sqlNotifyInsert = "Insert into Tep101_Jome17.NOTIFY "
									+ "(TYPE, NOTIFICATION_BODY, BODY_STATUS, MEMBER_ID) "
								+ "values "
									+ "(1, ?, 3, ?);";
		String sqlAttenderNoSelect = "Select "
										+ "ATTENDER_NO "
									+ "from Tep101_Jome17.ATTENDER "
									+ "where "
										+ "GROUP_ID = ? and MEMBER_ID = ? and ROLE = 2;";
		int insertResult = 0;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt1 = conn.prepareStatement(sqlAttenderInsert);
			PreparedStatement pstmt2 = conn.prepareStatement(sqlNotifyInsert);
			PreparedStatement pstmt3 = conn.prepareStatement(sqlAttenderNoSelect);) {
			conn.setAutoCommit(false);
			try {
				pstmt1.setString(1, bean.getGroupId());
				pstmt1.setInt(2, bean.getAttendStatus());
				pstmt1.setInt(3, bean.getRole());
				pstmt1.setString(4, bean.getMemberId());
				int insertAttenderResult = pstmt1.executeUpdate();		
				
				//取新增的attenderNO
				int attenderNo = -1;
				pstmt3.setString(1, bean.getGroupId());
				pstmt3.setString(2, bean.getMemberId());
				ResultSet rs = pstmt3.executeQuery();
				while(rs.next()) {
					attenderNo = rs.getInt(1);
				}
				if(insertAttenderResult < 1 || attenderNo < 1) {
					System.out.println(pstmt1);
					System.out.println( "insertAttenderResul: " + insertAttenderResult);
					System.out.println(pstmt3);
					System.out.println("attenderNo: " + attenderNo);
					throw new SQLException("Table Attender insert error! " + insertAttenderResult + attenderNo);
				}
				pstmt2.setString(1, attenderNo + "");
				pstmt2.setString(2, groupHeadId);
				int insertNotifyResult = pstmt2.executeUpdate();
				if(insertNotifyResult < 1) {
					throw new SQLException("Table Notify insert error!");
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
	 * 如果曾入團退團，現又再加入，先刪除前次的Attender 及 Notify
	 */
	public int hasJoinDelete (AttenderBean bean, String groupHeadId) {
		String sqlAttenderNoSelect = "Select "
										+ "ATTENDER_NO "
									+ "from Tep101_Jome17.ATTENDER "
										+ "where "
									+ "GROUP_ID = ? and MEMBER_ID = ? and ROLE = 2;";
		
		String sqlAttenderDelete = "DELETE from Tep101_Jome17.ATTENDER where ATTENDER_NO = ? ;";
		
		String sqlNotifyDelete = "DELETE from Tep101_Jome17.NOTIFY where TYPE = 1 and NOTIFICATION_BODY = ? and BODY_STATUS = 0;";
		int deleteResult = 0;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt0 = conn.prepareStatement(sqlAttenderNoSelect);
			PreparedStatement pstmt1 = conn.prepareStatement(sqlAttenderDelete);
			PreparedStatement pstmt2 = conn.prepareStatement(sqlNotifyDelete);) {
			conn.setAutoCommit(false);
			try {
				int attenderNo = -1;
				pstmt0.setString(1, bean.getGroupId());
				pstmt0.setString(2, bean.getMemberId());
				ResultSet rs0 = pstmt0.executeQuery();
				while(rs0.next()) {
					attenderNo = rs0.getInt(1);
				}
				if(attenderNo < 0) {
					System.out.println(pstmt0);
					deleteResult = -1;
					throw new SQLException("Table AttenderNo select error! " + deleteResult);
				}
				pstmt1.setInt(1, attenderNo);
				int deleteAttenderResult = pstmt1.executeUpdate();
				if(deleteAttenderResult < 1) {
					System.out.println(pstmt1);
					deleteResult = -2;
					throw new SQLException("Table Attender delete error! " + deleteResult);
				}
				pstmt2.setInt(1, attenderNo);
				int deletNotifyResult = pstmt2.executeUpdate();
				if(deletNotifyResult < 1) {
					System.out.println(pstmt2);
					deleteResult = -2;
					throw new SQLException("Table Notify delete error! " + deleteResult);
				}
				conn.commit();
				deleteResult = 1;
			}catch (SQLException e) {
				conn.rollback();
				e.printStackTrace();
			}	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return deleteResult;
	}
	
	/**
	 * 修改Attender表；團員變更參加狀態 (status3→1 , 3→2)→ delete notifiy(要求3)，增加notify(加入成功or失敗),
	 * 								(status1→0		)→ delete notifiy(成功1)，增加notify(團員 && 團長)
	 */
	public int updateAtender(AttenderBean bean, String headId) {
		String sqlAttenderUpdate = "update Tep101_Jome17.ATTENDER set "
										+ "ATTEDN_STATUS = ?, "
										+ "ROLE = ? "
									+"where "
										+ "ATTENDER_NO = ?";
		
		String sqlNotifyDelete = "delete from Tep101_Jome17.NOTIFY where TYPE = 1 and NOTIFICATION_BODY = ? ;";
		
		String sqlNotifyInsert = "Insert into Tep101_Jome17.NOTIFY "
									+ "(TYPE, NOTIFICATION_BODY, BODY_STATUS, MEMBER_ID) "
								+ "values "
									+ "(1, ?, ?, ?);";
		int updateAttenderResult = 0;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt1 = conn.prepareStatement(sqlAttenderUpdate);
			PreparedStatement pstmt2 = conn.prepareStatement(sqlNotifyDelete);
			PreparedStatement pstmt3 = conn.prepareStatement(sqlNotifyInsert); 
			PreparedStatement pstmt4 = conn.prepareStatement(sqlNotifyInsert);) {
			conn.setAutoCommit(false);
			try{
				pstmt1.setInt(1, bean.getAttendStatus());
				pstmt1.setInt(2, bean.getRole());
				pstmt1.setInt(3, bean.getAttenderNo());
				int attenderUpdateResult = pstmt1.executeUpdate();

				if (attenderUpdateResult < 1) {
					System.out.println(pstmt1);
					throw new SQLException("Table Attender update error! ");
				}
				pstmt2.setInt(1,bean.getAttenderNo());
				int notifyDeleteResult = pstmt2.executeUpdate();
				if(notifyDeleteResult < 1) {
					System.out.println(pstmt2);
					throw new SQLException("Table Notify delete error! ");
				}
				if(bean.getAttendStatus() == 0) {
					pstmt3.setString(1, bean.getAttenderNo()+ "");
					pstmt3.setInt(2, bean.getAttendStatus());
					pstmt3.setString(3, headId);
					int insertNotifyResultHead = pstmt3.executeUpdate();
					if(insertNotifyResultHead < 1) {
						System.out.println(pstmt3);
						throw new SQLException("Table notify for groupHead insert error! ");
					}
					pstmt4.setString(1, bean.getAttenderNo()+ "");
					pstmt4.setInt(2, bean.getAttendStatus());
					pstmt4.setString(3, bean.getMemberId());
					int insertNotifyResultSelf = pstmt4.executeUpdate();
					if(insertNotifyResultSelf < 1) {
						System.out.println(pstmt4);
						throw new SQLException("Table notify for groupMemer insert error! ");
					}
				}else {
					pstmt3.setString(1, bean.getAttenderNo()+ "");
					pstmt3.setInt(2, bean.getAttendStatus());
					pstmt3.setString(3, bean.getMemberId());
					int insertNotifyResultSelf = pstmt3.executeUpdate();
					System.out.println(pstmt3);
					if(insertNotifyResultSelf < 1) {
						System.out.println(pstmt3);
						throw new SQLException("Table notify for groupMember insert error! ");
					}
				}
				conn.commit();
				updateAttenderResult = 1;
			}catch (SQLException e) {
				conn.rollback();
				updateAttenderResult = -1;
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return updateAttenderResult;
	}
	
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
						+ " a.MEMBER_ID = ?  and a.GROUP_ID = ?;";	//a.MEMBER_ID  or a.GROUP_ID

		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, keyword);
			pstmt.setString(2, key);
			ResultSet rs = pstmt.executeQuery();
			PersonalGroup perGroup = new PersonalGroup();
			if(rs.next()) {
				perGroup.setMemberId(rs.getString("MEMBER_ID"));
				perGroup.setAttenderId(rs.getInt("ATTENDER_NO"));
				perGroup.setAttenderStatus(rs.getInt("ATTEDN_STATUS"));
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
							+ "on j.SURF_POINT_ID = s.SURF_POINT_ID "
					+ "where "
						+ "a.MEMBER_ID = ?  and a.ATTEDN_STATUS = 1 "
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
				pGroup.setAttenderStatus(rs.getInt("ATTEDN_STATUS"));
				pGroup.setRole(rs.getInt("ROLE"));							//6
				pGroup.setSurfName(rs.getString("SURF_NAME"));
				pGroup.setGroupId(rs.getString("GROUP_ID"));
				pGroup.setGroupName(rs.getString("GROUP_NAME"));			//9
				pGroup.setAssembleTime(rs.getString("ASSEMBLE_TIME"));
				pGroup.setGroupEndTime(rs.getString("GROUP_END_TIME"));
				pGroup.setSignUpEnd(rs.getString("SIGN_UP_END"));			//12
				pGroup.setGroupLimit(rs.getInt("GROUP_LIMIT"));
				pGroup.setNotice(rs.getString("NOTICE"));
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
	
	@Override
	public PersonalGroup selectRelation(PersonalGroup bean) {
		String sql = "SELECT * FROM Tep101_Jome17.ATTENDER where ATTENDER_NO = ?; ";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setInt(1, bean.getAttenderId());
			ResultSet rs = pstmt.executeQuery();
			PersonalGroup attenderGroup = new PersonalGroup();
			while(rs.next()) {
				attenderGroup.setAttenderId(rs.getInt("ATTENDER_NO"));
				attenderGroup.setGroupId(rs.getString("GROUP_ID"));
				attenderGroup.setAttenderStatus(rs.getInt("ATTEDN_STATUS"));
				attenderGroup.setRole(rs.getInt("ROLE"));
				attenderGroup.setMemberId(rs.getString("MEMBER_ID"));
			}
			return attenderGroup;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//用Attender的groupId 查詢 團長
	public AttenderBean selectGroupHeadId(String key) {
			String sql = "select "
							+ "*"
						+ "from "
							+ "Tep101_Jome17.ATTENDER  "
						+ "where "
							+ " ROLE = 1 and GROUP_ID = ?;";	

			try(Connection conn = dataSource.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);) {
				pstmt.setString(1, key);
				ResultSet rs = pstmt.executeQuery();
				AttenderBean attenderCaptain = new AttenderBean();
				if(rs.next()) {
					attenderCaptain.setAttenderNo(rs.getInt("ATTENDER_NO"));
					attenderCaptain.setGroupId(rs.getString("GROUP_ID"));
					attenderCaptain.setAttendStatus(rs.getInt("ATTEDN_STATUS"));
					attenderCaptain.setRole(rs.getInt("ROLE"));
					attenderCaptain.setMemberId(rs.getString("MEMBER_ID"));
				}
				return attenderCaptain;
			} catch (SQLException e) {
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
	public int deletaByKey(String key, String key1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<PersonalGroup> selectAllNoKey() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
