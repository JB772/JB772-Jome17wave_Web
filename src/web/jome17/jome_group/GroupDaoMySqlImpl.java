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
		int count = -1;
		String sql = "INSERT INTO JOIN_GROUP"
						+ "(GROUP_NAME, ASSEMBLE_TIME,"			//2
						+ "	GROUP_END_TIME, SIGN_UP_END,"		//4
						+ "	SURF_POINT_ID, GROUP_LIMIT,"		//6
						+ "	GENDER, NOTICE,"					//8
						+ "	IMAGE)"								//9 
					+ "VALUES"
						+ "(?, ?,"
						+ " ?, ?,"
						+ "	?, ?,"
						+ "	?, ?,"
						+ "	?);";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);){
			ps.setString(1, group.getGroupName());
			ps.setString(2, group.getAssembleTime());
			ps.setString(3, group.getGroupEndTime());
			ps.setString(4, group.getSignUpEnd());
			ps.setInt(5, group.getSurfPointId());
			ps.setInt(6, group.getGroupLimit());
			ps.setInt(7, group.getGender());
			ps.setString(8, group.getNotice());
			ps.setBytes(9, image);
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
			sql = "UPDATE JOIN_GROUP SET "
						+ "GROUP_NAME = ?, ASSEMBLE_TIME = ?, "		//2
						+ "GROUP_END_TIME = ?, SIGN_UP_END = ?, "	//4
						+ "SURF_POINT_ID = ?, GROUP_LIMIT = ?, "	//6
						+ "GENDER = ?, NOTICE = ?, "				//8
						+ "GROUP_STATUS = ?, IMAGE = ? "			//10
					+ "WHERE GROUP_ID = ?;";						//11
		} else {
			sql = "UPDATE JOIN_GROUP SET "
						+ "GROUP_NAME = ?, ASSEMBLE_TIME = ?, "		//2
						+ "GROUP_END_TIME = ?, SIGN_UP_END = ?, "	//4
						+ "SURF_POINT_ID = ?, GROUP_LIMIT = ?, "	//6
						+ "GENDER = ?, NOTICE = ?, "				//8
						+ "GROUP_STATUS = ? "						//9
					+ "WHERE GROUP_ID = ?;";						//10
		}
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, group.getGroupName());
			ps.setString(2, group.getAssembleTime());
			ps.setString(3, group.getGroupEndTime());
			ps.setString(4, group.getSignUpEnd());
			ps.setInt(5, group.getSurfPointId());
			ps.setInt(6, group.getGroupLimit());
			ps.setInt(7, group.getGender());
			ps.setString(8, group.getNotice());
			ps.setInt(9, group.getGroupStatus());
			if (image != null) {
				ps.setBytes(10, image);
				ps.setString(11, group.getGroupId());
			} else {
				ps.setString(10, group.getGroupId());
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
				String mId = rs.getString("GROUP_ID");
				String name = rs.getString("GROUP_NAME");
				String assembleTime = rs.getString("ASSEMBLE_TIME");
				String groupEndTime = rs.getString("GROUP_END_TIME");
				String signUpEnd = rs.getString("SIGN_UP_END");
				int surfPointId = rs.getInt("SURF_POINT_ID");
				int groupLimit = rs.getInt("GROUP_LIMIT");
				int gender = rs.getInt("GENDER");
				String notice = rs.getString("NOTICE");
				int groupStatus = rs.getInt("GROUP_STATUS");
				group = new Group(mId, name, assembleTime, groupEndTime, signUpEnd, surfPointId, groupLimit, gender, groupStatus, notice);
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
				String id = rs.getString("GROUP_ID");
				String name = rs.getString("GROUP_NAME");
				String assembleTime = rs.getString("ASSEMBLE_TIME");
				String groupEndTime = rs.getString("GROUP_END_TIME");
				String signUpEnd = rs.getString("SIGN_UP_END");
				int surfPointId = rs.getInt("SURF_POINT_ID");
				int groupLimit = rs.getInt("GROUP_LIMIT");
				int gender = rs.getInt("GENDER");
				String notice = rs.getString("NOTICE");
				int groupStatus = rs.getInt("GROUP_STATUS");
				Group group = new Group(id, name, assembleTime, groupEndTime, signUpEnd, surfPointId, groupLimit, gender, groupStatus, notice);
				groupList.add(group);
			}
			return groupList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groupList;
	}

}
