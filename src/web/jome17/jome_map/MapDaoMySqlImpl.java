package web.jome17.jome_map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.Soundbank;
import javax.sql.DataSource;

import web.jome17.main.ServiceLocator;



public class MapDaoMySqlImpl implements MapDao{
	DataSource dataSource;
	
	public MapDaoMySqlImpl() {
		dataSource = ServiceLocator.getInstance().getDataSource();
	}

	@Override
	public int inster(Map map, byte[] image) {
		int count = 0;
		String sql = "INSERT INTO SURF_POINT" + 
				"(name, side, type, direction, level, tidal, latitude, longitude, image) "	+ 
				"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setString(1, map.getName());
			ps.setString(2, map.getSide());
			ps.setString(3, map.getType());
			ps.setString(4, map.getDirection());
			ps.setString(5, map.getLevel());
			ps.setString(6, map.getTidal());
			ps.setDouble(7, map.getLatitude());
			ps.setDouble(8, map.getLongitude());
			ps.setBytes(9, image);
			count = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public int update(Map map, byte[] image) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(int id) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public byte[] getImage(int id) {
		String sql = "SELECT IMAGE FROM SURF_POINT WHERE SURF_POINT_ID = ?;";
		byte[] image = null;
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
//			System.out.println("124242");
			if (rs.next()) {
				image = rs.getBytes(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return image;
	}

	@Override
	public Map findById(int id) {
		String sql = "SELECT SURF_NAME, SURF_LAT, SURF_LNG, SURF_SIDE, "
				+ "SURF_TYPE, SURF_DIRECTION, LEVEL, TIDAL FROM SURF_POINT WHERE SURF_POINT_ID = ?;";
		//"SELECT name, side, type, direction, level, tidal, latitude, longitude FROM SURF_POINT WHERE id = ?;";
		
		Map map = null;
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ps.setInt(1, id);
			System.out.println("sql:" + sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String name = rs.getString("SURF_NAME");
				double latitude = rs.getDouble("SURF_LAT");
				double longitude = rs.getDouble("SURF_LNG");
				String side = rs.getString("SURF_SIDE");
				String type = rs.getString("SURF_TYPE");
				String direction = rs.getString("SURF_DIRECTION");
				String level = rs.getString("LEVEL");
				String tidal = rs.getString("TIDAL");
				System.out.println(tidal);

//				map = new Map(id, name, side, type, direction, level, tidal, latitude, longitude);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return map;
	}

	@Override
	public List<Map> getAll() {
		String sql = "SELECT * FROM SURF_POINT";
		List<Map> mapList = new ArrayList<>();
		try (Connection connection = dataSource.getConnection();
				PreparedStatement ps = connection.prepareStatement(sql);) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("SURF_POINT_ID");
				String name = rs.getString("SURF_NAME");
				String side = rs.getString("SURF_SIDE");
				String type = rs.getString("SURF_TYPE");
				String direction = rs.getString("SURF_DIRECTION");
				String level = rs.getString("LEVEL");
				String tidal = rs.getString("TIDAL");
				double latitude = rs.getDouble("SURF_LAT");
				double longitude = rs.getDouble("SURF_LNG");
				Map map = new Map(id, name, side, type, direction, level, tidal, latitude, longitude);
				mapList.add(map);
			}
			return mapList;
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return mapList;
	}

}
