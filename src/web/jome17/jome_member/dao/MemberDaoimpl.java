
package web.jome17.jome_member.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		MemberBean member = null;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, key);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				member = new MemberBean();
				member.setMember_id(rs.getString("ID"));
				member.setAccount(rs.getString("ACCOUNT"));
				member.setPassword(rs.getString("PASSWORD"));
				member.setNickname(rs.getString("NICKNAME"));
				member.setGender(rs.getInt("GENDER"));
				member.setPhone_number(rs.getString("PHONE_NUMBER"));
				member.setLatitude(rs.getDouble("LATITUDE"));
				member.setLongitude(rs.getDouble("LONTITUDE"));
				return member;
			} 
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
					+ "ID = ?";
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
					+ "ID = ?";
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
	public MemberBean login(String account, String password) {
		String sql = "select * from Tep101_Jome17.MEMBERINFO where ACCOUNT= ? and PASSWORD= ?;";
		MemberBean member = null;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, account);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				member = new MemberBean();
				member.setMember_id(rs.getString("ID"));
				member.setAccount(rs.getString("ACCOUNT"));
				member.setPassword(rs.getString("PASSWORD"));
				member.setNickname(rs.getString("NICKNAME"));
				member.setGender(rs.getInt("GENDER"));
				member.setPhone_number(rs.getString("PHONE_NUMBER"));
				member.setLatitude(rs.getDouble("LATITUDE"));
				member.setLongitude(rs.getDouble("LONTITUDE"));
				
				return member;
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return member;
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
}
