package railo.runtime.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import railo.commons.lang.StringUtil;


public class DataSourceUtil {

	public static boolean isMSSQL(DatasourceConnection dc) {
		try {
			if(dc.getConnection().getMetaData().getDatabaseProductName().indexOf("Microsoft")!=-1) return true;
		} 
		catch (SQLException e) {
			String className=dc.getDatasource().getClazz().getName();
			if(className.equals("com.microsoft.jdbc.sqlserver.SQLServerDriver") || className.equals("net.sourceforge.jtds.jdbc.Driver"))
				return true;
		}
		return false;
		
	}
	public static boolean isMSSQLDriver(DatasourceConnection dc) {
		try {
			if(dc.getConnection().getMetaData().getDriverName().indexOf("Microsoft SQL Server JDBC Driver")!=-1)
				return true;
		} 
		catch (SQLException e) {}
		
		String className=dc.getDatasource().getClazz().getName();
		return className.equals("com.microsoft.jdbc.sqlserver.SQLServerDriver");
	}

	public static boolean isValid(DatasourceConnection dc, int timeout) throws Throwable {
		return dc.getConnection().isValid(timeout); 
	}
	
	
	public static boolean isClosed(PreparedStatement ps, boolean defaultValue) {
		try {
			return ps.isClosed();
		} 
		catch (Throwable t) {
			return defaultValue;
		}
	}
	public static String getDatabaseName(DatasourceConnection dc) throws SQLException {
		String dbName=null;
		try {
			dbName = dc.getDatasource().getDatabase();
		} catch(Throwable t) {}
		if (StringUtil.isEmpty(dbName))
			dbName = dc.getConnection().getCatalog();  // works on most JDBC drivers (except Oracle )
		return dbName;
	}

}
