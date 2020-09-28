package web.jome17.jome_notify.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.sun.org.apache.regexp.internal.recompile;

import web.jome17.jome_notify.bean.Notify;
import web.jome17.main.ServiceLocator;

public class NotifyDaoImpl implements NotifyDao{
	DataSource  dataSource;
	

	public NotifyDaoImpl() {
		dataSource = ServiceLocator.getInstance().getDataSource();
	}


	@Override
	public int insert(Notify notify) {
		int count = 0;
		String sql = "Insert into NOTIFY (TYPE, NOTIFICATION_BODY) values(?, ?);";
		try (
			Connection connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(sql);
		){
			pstmt.setInt(1, notify.getType());
			pstmt.setString(2, notify.getNotificationBody());
			count = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}


	@Override
	public int update(Notify notify) {
		int count = 0;
		String sql = "update NOTIFY set NOTIFICATION_BODY = ? where NOTIFICATION_ID = ?;";
		try (
			Connection connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(sql);
		){
			pstmt.setString(1, notify.getNotificationBody());
			pstmt.setInt(2, notify.getNotificationId());
			count = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}


	@Override
	public List<Notify> getAll() {
		String sql = "select NOTIFICATION_ID, TYPE, NOTIFICATION_BODY from NOTIFY order by BUILD_DATE desc;";
		List<Notify> notifyList = new ArrayList<>();
		try (
			Connection connection = dataSource.getConnection();
			PreparedStatement pstmt = connection.prepareStatement(sql);
		){
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int notificationId = rs.getInt(1);
				int type = rs.getInt(2);
				String notificationBody = rs.getString(3);
				Notify notify = new Notify(notificationId, type, notificationBody);
				notifyList.add(notify);
			}
			return notifyList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return notifyList;
	}


}
