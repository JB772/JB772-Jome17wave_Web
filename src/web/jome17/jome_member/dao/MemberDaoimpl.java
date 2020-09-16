package web.jome17.jome_member.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.sql.DataSource;

import web.jome17.jome_member.bean.JomeMember;
import web.jome17.main.ServiceLocator;

public class MemberDaoimpl implements JomeMemberDao<JomeMember, String>{
	public DataSource datasource;
	public JomeMember member ;
	
	
	public MemberDaoimpl() {
		datasource = ServiceLocator.getInstance().getDataSource();
	}

	@Override
	public int insert(JomeMember bean) {
		String sql = "insert into MEMBER(ACCOUNT, PASSWORD, NICKNAME) "
				+ "values	(?,?,?)";
		try(Connection conn = datasource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setObject(1, bean.getAccount());
			pstmt.setObject(2, bean.getPassword());
			pstmt.setObject(3, bean.getNickname());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public JomeMember selectByKey(String key) {
		String sql = "select * from MEMBER where MEMBER_ID = ?";
		try(Connection conn = datasource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, key);
			ResultSet rs = pstmt.executeQuery();
			//return rs.next();
			if(rs.next()) {
				member.setAccount(rs.getString("account"));
				member.setPassword(rs.getString("password"));
				member.setNickname(rs.getString("nickName"));
				return member;
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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

	try(Connection conn = datasource.getConnection();
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

	@Override
	public JomeMember login(String account, String password) {
		String sql = "select * from EXAMPLE.MEMBER where ACCOUNT= ? and PASSWORD= ?;";
		try(Connection conn = datasource.getConnection();) {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				member.setAccount(rs.getString("account"));
				member.setPassword(password);
				member.setNickname(rs.getString("nickname"));
				rs.close();
				return member;
			}else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public byte[] getImage(String acconut) {
		String sql = "select image from EXAMPLE.MEMBER where ACCOUNT= ?";
		byte[] image = null;
		try(Connection conn = datasource.getConnection();
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

}
