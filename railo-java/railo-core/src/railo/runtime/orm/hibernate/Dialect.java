package railo.runtime.orm.hibernate;

import railo.commons.lang.StringUtil;
import railo.runtime.db.DataSource;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class Dialect {
	private static Struct dialects=new StructImpl();
	
	static {
		
		dialects.setEL("Cache71", org.hibernate.dialect.Cache71Dialect.class.getName());
		dialects.setEL("Caché 2007.1", org.hibernate.dialect.Cache71Dialect.class.getName());
		dialects.setEL("Cache 2007.1", org.hibernate.dialect.Cache71Dialect.class.getName());
		
		dialects.setEL("DataDirectOracle9", org.hibernate.dialect.DataDirectOracle9Dialect.class.getName());

		
		dialects.setEL("DB2390", org.hibernate.dialect.DB2390Dialect.class.getName());
	    dialects.setEL("DB2/390", org.hibernate.dialect.DB2390Dialect.class.getName());
	    dialects.setEL("DB2OS390", org.hibernate.dialect.DB2390Dialect.class.getName());
		dialects.setEL("DB2400", org.hibernate.dialect.DB2400Dialect.class.getName());
		dialects.setEL("DB2/400", org.hibernate.dialect.DB2400Dialect.class.getName());
		dialects.setEL("DB2AS400", org.hibernate.dialect.DB2400Dialect.class.getName());
		dialects.setEL("DB2", org.hibernate.dialect.DB2Dialect.class.getName());
		dialects.setEL("com.ddtek.jdbc.db2.DB2Driver", org.hibernate.dialect.DB2Dialect.class.getName());
		
		dialects.setEL("Derby", org.hibernate.dialect.DerbyDialect.class.getName());
		dialects.setEL("Firebird", org.hibernate.dialect.FirebirdDialect.class.getName());
		dialects.setEL("org.firebirdsql.jdbc.FBDriver", org.hibernate.dialect.FirebirdDialect.class.getName());
		dialects.setEL("FrontBase", org.hibernate.dialect.FrontBaseDialect.class.getName());
		
		dialects.setEL("H2", org.hibernate.dialect.H2Dialect.class.getName());
		dialects.setEL("org.h2.Driver", org.hibernate.dialect.H2Dialect.class.getName());
		dialects.setEL("H2DB", org.hibernate.dialect.H2Dialect.class.getName());
		dialects.setEL("HSQL", org.hibernate.dialect.HSQLDialect.class.getName());
		dialects.setEL("HSQLDB", org.hibernate.dialect.HSQLDialect.class.getName());
		dialects.setEL("org.hsqldb.jdbcDriver", org.hibernate.dialect.HSQLDialect.class.getName());
		
		dialects.setEL("Informix", org.hibernate.dialect.InformixDialect.class.getName());
		dialects.setEL("Ingres", org.hibernate.dialect.IngresDialect.class.getName());
		dialects.setEL("Interbase", org.hibernate.dialect.InterbaseDialect.class.getName());
		dialects.setEL("JDataStore", org.hibernate.dialect.JDataStoreDialect.class.getName());
		dialects.setEL("Mckoi", org.hibernate.dialect.MckoiDialect.class.getName());
		dialects.setEL("MckoiSQL", org.hibernate.dialect.MckoiDialect.class.getName());
		dialects.setEL("Mimer", org.hibernate.dialect.MimerSQLDialect.class.getName());
		dialects.setEL("MimerSQL", org.hibernate.dialect.MimerSQLDialect.class.getName());
		
		dialects.setEL("MySQL5", org.hibernate.dialect.MySQL5Dialect.class.getName());
		dialects.setEL("MySQL5InnoDB", org.hibernate.dialect.MySQL5InnoDBDialect.class.getName());
		dialects.setEL("MySQL5/InnoDB", org.hibernate.dialect.MySQL5InnoDBDialect.class.getName());
		dialects.setEL("MySQL", org.hibernate.dialect.MySQLDialect.class.getName());
		dialects.setEL("org.gjt.mm.mysql.Driver", org.hibernate.dialect.MySQLDialect.class.getName());
		dialects.setEL("MySQLInnoDB", org.hibernate.dialect.MySQLInnoDBDialect.class.getName());
		dialects.setEL("MySQL/InnoDB", org.hibernate.dialect.MySQLInnoDBDialect.class.getName());
		dialects.setEL("MySQLwithInnoDB", org.hibernate.dialect.MySQLInnoDBDialect.class.getName());
		dialects.setEL("MySQLMyISAM", org.hibernate.dialect.MySQLMyISAMDialect.class.getName());
		dialects.setEL("MySQL/MyISAM", org.hibernate.dialect.MySQLMyISAMDialect.class.getName());
		dialects.setEL("MySQLwithMyISAM", org.hibernate.dialect.MySQLMyISAMDialect.class.getName());
		
		dialects.setEL("Oracle10g", org.hibernate.dialect.Oracle10gDialect.class.getName());
		dialects.setEL("Oracle8i", org.hibernate.dialect.Oracle8iDialect.class.getName());
		dialects.setEL("Oracle9", org.hibernate.dialect.Oracle9Dialect.class.getName());
		dialects.setEL("Oracle9i", org.hibernate.dialect.Oracle9iDialect.class.getName());
		dialects.setEL("Oracle", org.hibernate.dialect.OracleDialect.class.getName());
		dialects.setEL("oracle.jdbc.driver.OracleDriver", org.hibernate.dialect.OracleDialect.class.getName());
		dialects.setEL("Pointbase", org.hibernate.dialect.PointbaseDialect.class.getName());
		dialects.setEL("PostgresPlus", org.hibernate.dialect.PostgresPlusDialect.class.getName());
		dialects.setEL("PostgreSQL", org.hibernate.dialect.PostgreSQLDialect.class.getName());
		dialects.setEL("org.postgresql.Driver", org.hibernate.dialect.PostgreSQLDialect.class.getName());
		dialects.setEL("Progress", org.hibernate.dialect.ProgressDialect.class.getName());
		
		dialects.setEL("SAPDB", org.hibernate.dialect.SAPDBDialect.class.getName());
		
		dialects.setEL("SQLServer", org.hibernate.dialect.SQLServerDialect.class.getName());
		dialects.setEL("MSSQL", org.hibernate.dialect.SQLServerDialect.class.getName());
		dialects.setEL("MicrosoftSQLServer", org.hibernate.dialect.SQLServerDialect.class.getName());
		dialects.setEL("com.microsoft.jdbc.sqlserver.SQLServerDriver", org.hibernate.dialect.SQLServerDialect.class.getName());
		
		dialects.setEL("Sybase11", org.hibernate.dialect.Sybase11Dialect.class.getName());
		dialects.setEL("SybaseAnywhere", org.hibernate.dialect.SybaseAnywhereDialect.class.getName());
		dialects.setEL("SybaseASE15", org.hibernate.dialect.SybaseASE15Dialect.class.getName());
		dialects.setEL("Sybase", org.hibernate.dialect.SybaseDialect.class.getName());
    }
	
	/**
	 * return a SQL dialect that match the given Name
	 * @param name
	 * @return
	 */
	public static String getDialect(DataSource ds){
		String name=ds.getClazz().getName();
		if("net.sourceforge.jtds.jdbc.Driver".equals(name)){
			String dsn=ds.getDsnTranslated();
			if(StringUtil.indexOfIgnoreCase(dsn, "sybase")!=-1)
				return getDialect("SQLServer");
		}
		return getDialect(name);
	}
	
	public static String getDialect(String name){
		if(StringUtil.isEmpty(name))return null;
		return (String) dialects.get(name, null);
	}
}
