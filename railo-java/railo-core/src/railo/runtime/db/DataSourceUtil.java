/**
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 **/
package railo.runtime.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import railo.commons.lang.StringUtil;


public class DataSourceUtil {
	
	
	public static boolean isHSQLDB(DatasourceConnection dc) {
		try {
			if(dc.getConnection().getMetaData().getDatabaseProductName().indexOf("HSQL")!=-1) return true;
		} 
		catch (SQLException e) {
			String className=dc.getDatasource().getClazz().getName();
			if(className.equals("org.hsqldb.jdbcDriver"))
				return true;
		}
		return false;
	}
    
    
    public static boolean isOracle(DatasourceConnection dc) {
		try {
			if(dc.getConnection().getMetaData().getDatabaseProductName().indexOf("Oracle")!=-1) return true;
		} 
		catch (SQLException e) {
			String className=dc.getDatasource().getClazz().getName();
			if(className.indexOf("OracleDriver")!=-1)
				return true;
		}
		return false;
	}
    
	public static boolean isMySQL(DatasourceConnection dc) {
		try {
			if(dc.getConnection().getMetaData().getDatabaseProductName().indexOf("MySQL")!=-1) return true;
		} 
		catch (SQLException e) {
			String className=dc.getDatasource().getClazz().getName();
			if(className.equals("org.gjt.mm.mysql.Driver"))
				return true;
		}
		return false;
	}
	
	
    
	public static boolean isMSSQL(DatasourceConnection dc) {
		try {
			if(dc.getConnection().getMetaData().getDatabaseProductName().indexOf("Microsoft")!=-1) return true;
		} 
		catch (SQLException e) {
			String className=dc.getDatasource().getClazz().getName();
			if(className.equals("com.microsoft.jdbc.sqlserver.SQLServerDriver") || 
					className.equals("net.sourceforge.jtds.jdbc.Driver"))
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
