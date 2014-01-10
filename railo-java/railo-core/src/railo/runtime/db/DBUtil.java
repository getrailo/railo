package railo.runtime.db;

import railo.commons.lang.StringUtil;

public class DBUtil {
	
	private static DataSourceDefintion DB2=new DataSourceDefintion("com.ddtek.jdbc.db2.DB2Driver","jdbc:datadirect:db2://{host}:{port};DatabaseName={database}",50000);
	private static DataSourceDefintion FIREBIRD=new DataSourceDefintion("org.firebirdsql.jdbc.FBDriver","jdbc:firebirdsql://{host}:{port}/{path}{database}",3050);
	private static DataSourceDefintion H2=new DataSourceDefintion("org.h2.Driver","jdbc:h2:{path}{database};MODE={mode}",-1);
	private static DataSourceDefintion MSSQL=new DataSourceDefintion("net.sourceforge.jtds.jdbc.Driver","jdbc:jtds:sqlserver://{host}:{port}/{database}",1433);
	private static DataSourceDefintion MYSQL=new DataSourceDefintion("org.gjt.mm.mysql.Driver","jdbc:mysql://{host}:{port}/{database}",3306);
	private static DataSourceDefintion ORACLE=new DataSourceDefintion("oracle.jdbc.driver.OracleDriver","jdbc:oracle:{drivertype}:@{host}:{port}:{database}",1521);
	private static DataSourceDefintion POSTGRESQL=new DataSourceDefintion("org.postgresql.Driver","jdbc:postgresql://{host}:{port}/{database}",5432);
	private static DataSourceDefintion SYBASE=new DataSourceDefintion("net.sourceforge.jtds.jdbc.Driver","jdbc:jtds:sybase://{host}:{port}/{database}",7100);
	
	
	public static DataSourceDefintion getDataSourceDefintionForType(String type, DataSourceDefintion defaultValue){
		if(StringUtil.isEmpty(type)) return defaultValue;
		type=type.trim().toLowerCase();
		// TODO this needs to be loaded dynamically from 
		if("db2".equals(type)) return DB2;
		if("firebird".equals(type)) return FIREBIRD;
		if("h2".equals(type)) return H2;
		if("mssql".equals(type)) return MSSQL;
		if("mysql".equals(type)) return MYSQL;
		if("oracle".equals(type)) return ORACLE;
		if("postgresql".equals(type) || "postgre".equals(type)) return POSTGRESQL;
		if("sybase".equals(type)) return SYBASE;
		return defaultValue;
	}
	public static class DataSourceDefintion{
		
		public final String className;
		public final String connectionString;
		public final int port;
		
		DataSourceDefintion(String className, String connectionString, int port){
			this.className=className;
			this.connectionString=connectionString;
			this.port=port;
		}
	}
}