package web.jome17.jome_group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.sun.org.apache.regexp.internal.recompile;

import web.jome17.jome_map.Map;
import web.jome17.main.ServiceLocator;

public class GroupDaoMySqlImpl implements GroupDao{
	DataSource dataSource;
	
	public GroupDaoMySqlImpl() {
		dataSource = ServiceLocator.getInstance().getDataSource();
	}

	@Override
	public int inster(Group group, byte[] image) {
		int count = 0;
		String sql = "INSERT INTO JOIN_GROUP"
				+ "(GROUP_NAME, ASSEMBLE_TIME, GROUP_END_TIME, SIGN_UP_END, MODIFY_TIME, SURF_POINT_ID, GROUP_LIMIT, GENDER,"
				+ "NOTICE, IMAGE, GROUP_STATUS)" 
				+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);){
			ps.setString(1, group.getName());
			ps.setTimestamp(2, group.getAssembleTime());
			ps.setTimestamp(3, group.getGroupEndTime());
			ps.setTimestamp(4, group.getSignUpEnd());
			ps.setTimestamp(5, group.getModifyTime());
			ps.setInt(6, group.getSurfPointId());
			ps.setInt(7, group.getGroupLimit());
			ps.setInt(8, group.getGender());
			ps.setString(9, group.getNotice());
			ps.setBytes(10, image);
			ps.setInt(11, group.getGroupStatus());
			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return count;
	}

	@Override
	public int update(Group group, byte[] image) {
		int count = 0;
		String sql = "";
		if (image != null) {
			sql = "UPDATE JOIN_GROUP SET GROUP_NAME = ?, ASSEMBLE_TIME = ?, GROUP_END_TIME = ?, SIGN_UP_END = ?, MODIFY_TIME = ?, "
					+ "SURF_POINT_ID = ?, GROUP_LIMIT = ?, GENDER = ?, NOTICE = ?, GROUP_STATUS = ?, IMAGE = ? WHERE GROUP_ID = ?;";
		} else {
			sql = "UPDATE JOIN_GROUP SET GROUP_NAME = ?, ASSEMBLE_TIME = ?, GROUP_END_TIME = ?, SIGN_UP_END = ?, MODIFY_TIME = ?, "
					+ "SURF_POINT_ID = ?, GROUP_LIMIT = ?, GENDER = ?, NOTICE = ?, GROUP_STATUS = ? WHERE GROUP_ID = ?;";
		}
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, group.getName());
			ps.setTimestamp(2, group.getAssembleTime());
			ps.setTimestamp(3, group.getGroupEndTime());
			ps.setTimestamp(4, group.getSignUpEnd());
			ps.setTimestamp(5, group.getModifyTime());
			ps.setInt(6, group.getSurfPointId());
			ps.setInt(7, group.getGroupLimit());
			ps.setInt(8, group.getGender());
			ps.setString(9, group.getNotice());
			ps.setInt(10, group.getGroupStatus());
			if (image != null) {
				ps.setBytes(11, image);
				ps.setInt(12, group.getId());
			} else {
				ps.setInt(11, group.getId());
			}
			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public int delete(int id) {
		int count = 0;
		String sql = "DELETE FROM JOIN_GROUP WHERE GROUP_ID = ?;";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setInt(1,id);
			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	

	@Override
	public byte[] getImage(int id) {
		String sql = "SELECT IMAGE FROM JOIN_GROUP WHERE GROUP_ID = ?;";
		byte[] image = null;
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				image = rs.getBytes(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return image;
	}

	@Override
	public Group findById(int id) {
		String sql = "SELECT GROUP_NAME, ASSEMBLE_TIME, GROUP_END_TIME, SIGN_UP_END, MODIFY_TIME, SURF_POINT_ID, GROUP_LIMIT, GENDER,"
				+ "NOTICE, GROUP_STATUS FROM JOIN_GROUP WHERE GROUP_ID = ?;";
		Group group = null;
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String name = rs.getString(1);
				Timestamp assembleTime = rs.getTimestamp(2);
				Timestamp groupEndTime = rs.getTimestamp(3);
				Timestamp signUpEnd = rs.getTimestamp(4);
				Timestamp modifyTime = rs.getTimestamp(5);
				Integer surfPointId = rs.getInt(6);
				Integer groupLimit = rs.getInt(7);
				Integer gender = rs.getInt(8);
				String notice = rs.getString(9);
				Integer groupStatus = rs.getInt(10);
				group = new Group(id, name, assembleTime, groupEndTime, signUpEnd, modifyTime, surfPointId, groupLimit, gender, notice, groupStatus);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return group;
	}

	@Override
	public List<Group> getAll() {
		String sql = "SELECT * FROM JOIN_GROUP";
		List<Group> groupList = new ArrayList<>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("GROUP_ID");
				String name = rs.getString("GROUP_NAME");
				Timestamp assembleTime = rs.getTimestamp("ASSEMBLE_TIME");
				Timestamp groupEndTime = rs.getTimestamp("GROUP_END_TIME");
				Timestamp signUpEnd = rs.getTimestamp("SIGN_UP_END");
				Timestamp modifyTime = rs.getTimestamp("MODIFY_TIME");
				int surfPointId = rs.getInt("SURF_POINT_ID");
				int groupLimit = rs.getInt("GROUP_LIMIT");
				int gender = rs.getInt("GENDER");
				String notice = rs.getString("NOTICE");
				int groupStatus = rs.getInt("GROUP_STATUS");
				Group group = new Group(id, name, assembleTime, groupEndTime, signUpEnd, modifyTime, surfPointId, groupLimit, gender, notice, groupStatus);
				groupList.add(group);
			}
			return groupList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groupList;
	}

}
