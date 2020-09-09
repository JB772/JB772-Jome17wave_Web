package web.jome17.main;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class ServiceLocator {
	
	private Context initalContext;

	private static ServiceLocator serviceLocator = new ServiceLocator();

	public static ServiceLocator getInstance() {
		return serviceLocator;
	}

	private ServiceLocator() {
		try {
			this.initalContext = new InitialContext();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public DataSource getDataSource() {
		DataSource dataSource = null;
		try {
			Context ctx = (Context) initalContext.lookup("java:comp/env");
			// 找到context裡面的 Resource name="jdbc/Spots"
			dataSource = (DataSource) ctx.lookup("jdbc/Tep101_Jome17");
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return dataSource;
	}
	
	public DataSource getDataSource(String dataSourceName) {
		DataSource datasource = null;
		try {
			Context ctx = (Context) initalContext.lookup("java:comp/env");
			datasource = (DataSource) ctx.lookup(dataSourceName);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return datasource;
	}

}
