package web.jome17.jome_member.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import web.jome17.jome_member.bean.AdminBean;
import web.jome17.main.ServiceLocator;

public class AdminDaoimpl implements CommonDao<AdminBean, String>{
	
	public DataSource dataSource;
	
	public AdminDaoimpl () {
		dataSource = ServiceLocator.getInstance().getDataSource();
	}

	@Override
	public int update(AdminBean bean) {
		String sql = "";
		byte[] image = bean.getImage();
		if (image == null) {
			sql = "update Tep101_Jome17.Admin set " 
						+ "ADMINACCOUNT = ?, " 		//1
						+ "ADMINPASSWORD = ?, " 	//2
						+ "LOGINCOUNT = ? "		//3
				+ "where " 
						+ "ADMINID = ? ;";
		} else {
			sql = "update Tep101_Jome17.Admin set " 
					+ "ADMINACCOUNT = ?, " 			//1
					+ "ADMINPASSWORD = ?, " 		//2
					+ "LOGINCOUNT = ?, "			//3
					+ "IMAGE = ? "					//4
				+ "where " 	
					+ "ADMINID = ? ;";
		}
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, bean.getAdminAccount());
			pstmt.setString(2, bean.getAdminPassword());
			pstmt.setInt(3, bean.getLoginCount());
			if(image == null) {
				pstmt.setInt(4, bean.getAdminId());
			}else {
				pstmt.setBytes(4, image);
				pstmt.setInt(5, bean.getAdminId());
			}
			System.out.println("AdminUpdate: "+ pstmt);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public AdminBean selectByKey(String keyword, String key) {
		String sql = "select * from Tep101_Jome17.Admin where " + keyword + " = ?";
		try(Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);) {
			pstmt.setString(1, key);
			ResultSet rs = pstmt.executeQuery();
			AdminBean selectAdmin = null;
			while(rs.next()) {
				int id = rs.getInt("adminId");
				String account = rs.getString("adminAccount");
				String password = rs.getString("adminPassword");
				int loginCount = rs.getInt("loginCount");
				String lastLoginTime = rs.getString("lastLoginTime");
				selectAdmin = new AdminBean(id, account, password, loginCount, lastLoginTime);
			}
			return  selectAdmin;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public byte[] getImage(String acconut) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int insert(AdminBean bean) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * 以下暫無用
	 */
	
	@Override
	public AdminBean selectRelation(AdminBean bean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AdminBean> selectAll(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AdminBean> selectAllNoKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int deletaByKey(String key, String key1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCount(String memberId) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
