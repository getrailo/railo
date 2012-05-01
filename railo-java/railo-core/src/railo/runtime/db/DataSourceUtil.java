package railo.runtime.db;

import java.sql.PreparedStatement;

public class DataSourceUtil {

	public static boolean isMSSQL(DatasourceConnection dc) {
		String className=dc.getDatasource().getClazz().getName();
		return className.equals("com.microsoft.jdbc.sqlserver.SQLServerDriver") || className.equals("net.sourceforge.jtds.jdbc.Driver");
	}
	public static boolean isMSSQLDriver(DatasourceConnection dc) {
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

}
