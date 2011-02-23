package railo.runtime.db;

import java.lang.reflect.Method;
import java.sql.Connection;

import railo.runtime.op.Caster;

public class DataSourceUtil {
	private static Method IS_VALID;

	public static boolean isMSSQL(DatasourceConnection dc) {
		
		String className=dc.getDatasource().getClazz().getName();
		return className.equals("com.microsoft.jdbc.sqlserver.SQLServerDriver") || className.equals("net.sourceforge.jtds.jdbc.Driver");
	}

	public static boolean isValid(DatasourceConnection dc, int timeout) throws Throwable {
		// JDK5 only
		Connection c = dc.getConnection();
		if(IS_VALID==null)IS_VALID=c.getClass().getMethod("isValid", new Class[]{int.class});
		return Caster.toBooleanValue(IS_VALID.invoke(c, new Object[]{Integer.valueOf(timeout)}));
		////////////////////////////////
		
		/*
		 JDK6: replace code in this method with
		 return dc.getConnection().isValid(1000);
		 */	
	}

}
