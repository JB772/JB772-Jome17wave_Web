
package web.jome17.jome_member.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import web.jome17.jome_member.bean.MemberBean;
import web.jome17.main.ServiceLocator;

public class MemberDaoimpl implements CommonDao<MemberBean, String>{
	public DataSource dataSource;
	
	public MemberDaoimpl() {
		dataSource = ServiceLocator.getInstance().getDataSource();
	}

	@Override
	public int insert(MemberBean bean) {
		String sql = "insert into MEMBERINFO(ID, ACCOUNT, PASSWORD, NICKNAME, GENDER, PHONE_NUMBER) "
					+ "values	(?,?,?,?,?,?)";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setObject(1, bean.getMember_id());
			pstmt.setObject(2, bean.getAccount());
			pstmt.setObject(3, bean.getPassword());
			pstmt.setObject(4, bean.getNickname());
			pstmt.setObject(5, bean.getGender());
			pstmt.setObject(6, bean.getPhone_number());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
//										keyword = ID or ACCOUNT
	@Override
	public MemberBean selectByKey(String keyword, String key) {
		String sql = "select * from MEMBERINFO where "+ keyword +" = ?";
		String sqlFriendConut = "select count(*) "
								+ "from Tep101_Jome17.FRIEND_LIST "
								+ "where "
									+ "(INVITE_M_ID = ? or ACCEPT_M_ID = ?) "
									+ "and FRIEND_STATUS = 1;";
		String sqlAverageScore ="select "
									+ "avg(RATING_SCORE), "
									+ "COUNT(*) "
								+ "from "
									+ "Tep101_Jome17.SCORE "
								+ "where "
									+ "BE_RATED_ID = ?;";
		String sqlAddGroupCount = "select count(*) "
								+ "from Tep101_Jome17.ATTENDER "
								+ "where "
									+ "MEMBER_ID = ? "
									+ "and ROLE = 1 and ATTEDN_STATUS = 1;" ;
		String sqlGroupCount = "select count(*) "
								+ "from Tep101_Jome17.ATTENDER "
								+ "where "
									+ "MEMBER_ID = ? "
									+ "and ROLE = 2 and ATTEDN_STATUS = 1;" ;
		MemberBean member = null;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			PreparedStatement pstmt1 = conn.prepareStatement(sqlFriendConut);
			PreparedStatement pstmt2 = conn.prepareStatement(sqlAverageScore);
			PreparedStatement pstmt3 = conn.prepareStatement(sqlAddGroupCount);
			PreparedStatement pstmt4 = conn.prepareStatement(sqlGroupCount);) {
			//取member基本資料
			pstmt.setString(1, key);
			ResultSet rs = pstmt.executeQuery();
			member = new MemberBean();
			if(rs.next()) {
				member.setMember_id(rs.getString("ID"));
				member.setAccount(rs.getString("ACCOUNT"));
				member.setPassword(rs.getString("PASSWORD"));
				member.setNickname(rs.getString("NICKNAME"));
				member.setGender(rs.getInt("GENDER"));
				member.setPhone_number(rs.getString("PHONE_NUMBER"));
				member.setLatitude(rs.getDouble("LATITUDE"));
				member.setLongitude(rs.getDouble("LONTITUDE"));				
			} 
			String memberId = member.getMember_id();
			//取好友數
			pstmt1.setString(1, memberId);
			pstmt1.setString(2, memberId);
			ResultSet rs1 = pstmt1.executeQuery();
			if(rs1.next()) {
				member.setFriendCount(String.valueOf(rs1.getInt(1)));
			}
			//取平均評分
			pstmt2.setString(1, memberId);
			ResultSet rs2 = pstmt2.executeQuery();
			if(rs2.next()) {
				Double d = rs2.getDouble(1);
				BigDecimal bd = new BigDecimal(d);
				bd = bd.setScale(1, 4);
				member.setScoreAverage(String.valueOf(bd));
				member.setBeRankedCount(String.valueOf(rs2.getInt(2)));
			}
			//取得主揪次數
			pstmt3.setString(1, memberId);
			ResultSet rs3 = pstmt3.executeQuery();
			if(rs3.next()) {
				member.setCreateGroupCount(String.valueOf(rs3.getInt(1)));
			}
			//取得入團次數
			pstmt4.setString(1, memberId);
			ResultSet rs4 = pstmt4.executeQuery();
			if(rs4.next()) {
				member.setGroupCount(String.valueOf(rs4.getInt(1)));
			}
			return member;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return member;
	}

	@Override
	public int update(MemberBean bean) {
		byte[] image = bean.getImage();
		String sql = "";
		if(image == null) {
			sql = "update Tep101_Jome17.MEMBERINFO set "
					+ "ACCOUNT = ?, "
					+ "PASSWORD = ?, "
					+ "NICKNAME = ?, "		//3
					+ "GENDER = ?, "
					+ "PHONE_NUMBER = ?, "
					+ "LATITUDE = ?, "		//6
					+ "LONTITUDE = ? "
				+ "where "
					+ "ID = ?";				//8
		}else {
			sql = "update Tep101_Jome17.MEMBERINFO set "
					+ "ACCOUNT = ?, "
					+ "PASSWORD = ?, "
					+ "NICKNAME = ?, "		//3
					+ "GENDER = ?, "
					+ "PHONE_NUMBER = ?, "
					+ "LATITUDE = ?, "		//6
					+ "LONTITUDE = ?, "
					+ "IMAGE = ? "
				+ "where "
					+ "ID = ?";				//9
		}
	try(Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);) {
		pstmt.setString(1, bean.getAccount());
		pstmt.setString(2, bean.getPassword());
		pstmt.setString(3, bean.getNickname());
		pstmt.setInt(4, bean.getGender());
		pstmt.setString(5, bean.getPhone_number());
		pstmt.setDouble(6, bean.getLatitude());
		pstmt.setDouble(7, bean.getLongitude());
		if(image == null) {
			pstmt.setString(8, bean.getMember_id());
		}else {
			pstmt.setBytes(8, image);
			pstmt.setString(9, bean.getMember_id());
		}
		
		return pstmt.executeUpdate();
	} catch (Exception e) {
		e.printStackTrace();
	}
	return -1;
	}
	
	@Override
	public byte[] getImage(String memberId) {
		String sql = "select IMAGE from Tep101_Jome17.MEMBERINFO where ID = ?;";
		byte[] image = null;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, memberId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				image = rs.getBytes("image");
			}
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return image;
	}

	@Override
	public List<MemberBean> selectAll(String memberId) {
		String sql = "SELECT "
						+ "ID, "
						+ "NICKNAME, "
						+ "LATITUDE, "
						+ "LONTITUDE "
					+ "FROM "
						+ "Tep101_Jome17.MEMBERINFO;";
		List<MemberBean> members = new ArrayList<MemberBean>();
		try(Connection conn = dataSource.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();) {
				while(rs.next()) {
					MemberBean member = new MemberBean();
					member.setMember_id(rs.getString("ID"));
					member.setNickname(rs.getString("NICKNAME"));
					member.setLatitude(rs.getDouble("LATITUDE"));
					member.setLongitude(rs.getDouble("LONTITUDE"));
					members.add(member);
				}
				return members;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return members;
	}

	@Override
	public String getCount(String memberId) {
		return null;
	}
	
	
	/*
	 * 暫不需實作方法
	 */
	@Override
	public MemberBean selectRelation(MemberBean bean) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int deletaByKey(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<MemberBean> selectAllNoKey() {
		// TODO Auto-generated method stub
		return null;
	}
}
