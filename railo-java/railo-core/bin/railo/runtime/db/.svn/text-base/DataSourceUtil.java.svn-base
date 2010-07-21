package railo.runtime.db;

public class DataSourceUtil {
	public static boolean isMSSQL(DatasourceConnection dc) {
		
		String className=dc.getDatasource().getClazz().getName();
		return className.equals("com.microsoft.jdbc.sqlserver.SQLServerDriver") || className.equals("net.sourceforge.jtds.jdbc.Driver");
	}

}
