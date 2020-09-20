package web.jome17.jome_member.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import javax.sql.DataSource;
import web.jome17.jome_member.bean.JomeMember;
import web.jome17.main.ServiceLocator;

public class MemberDaoimpl implements CommonDao<JomeMember, String>{
	public DataSource dataSource;
	
	public MemberDaoimpl() {
		dataSource = ServiceLocator.getInstance().getDataSource();
	}

	@Override
	public int insert(JomeMember bean) {
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
//			System.out.println("# insert sql: " + pstmt.toString());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public JomeMember selectByKey(String key) {
		String sql = "select * from MEMBERINFO where ACCOUNT = ?";
		JomeMember member = null;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, key);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				member = new JomeMember();
				member.setMember_id(rs.getString("ID"));
				member.setAccount(rs.getString("ACCOUNT"));
				member.setPassword(rs.getString("PASSWORD"));
				member.setNickname(rs.getString("NICKNAME"));
				member.setGender(rs.getInt("GENDER"));
				member.setPhone_number(rs.getString("PHONE_NUMBER"));
				return member;
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return member;
	}

	@Override
	public List<JomeMember> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(JomeMember bean) {
		String sql = "update Member set "
				+ "PASSWORD = ?, "
				+ "NICKNAME = ? "
				+"where ACCOUNT = ?";

	try(Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);) {
		pstmt.setString(1, bean.getPassword());
		pstmt.setString(2, bean.getNickname());
		pstmt.setString(3, bean.getAccount());
		return pstmt.executeUpdate();
	} catch (Exception e) {
		e.printStackTrace();
	}
	return -1;
	}

	@Override
	public int deletaByKey(String key) {
		// TODO Auto-generated method stub
		return 0;
	}

//	@Override
//	public JomeMember login(String account, String password) {
//		String sql = "select * from Tep101_Jome17.MEMBERINFO where ACCOUNT= ? and PASSWORD= ?;";
//		try(Connection conn = dataSource.getConnection();) {
//			PreparedStatement pstmt = conn.prepareStatement(sql);
//			ResultSet rs = pstmt.executeQuery();
//			if(rs.next()) {
//				member.setAccount(rs.getString("ACCOUNT"));
//				member.setPassword("PASSWORD");
//				member.setNickname(rs.getString("NICKNAME"));
//				rs.close();
//				
//				System.out.println("memberNickName"+member.getNickname());
//				return member;
//			}else {
//				return null;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	@Override
	public byte[] getImage(String acconut) {
		String sql = "select image from EXAMPLE.MEMBER where ACCOUNT= ?";
		byte[] image = null;
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, acconut);
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
	public JomeMember selectRelation(String key1, String key2) {
		// TODO Auto-generated method stub
		return null;
	}

}
