package railo.commons.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.SQL;

/**
 * Utility for db
 */
public final class DBUtil {
	
	// TODO impl. this class, not used at the moment
	/**
	 * returns label matching className
	 * @param className
	 * @return label
	 */
	public static String getLabelForDriverClass(String className) {
		if("com.microsoft.jdbc.sqlserver.SQLServerDriver".equals(className))
			return "MSSQL DataBase";
		// TODO connect WS from railo. ch to get more
		return className;
	}

	public static void setAutoCommitEL(Connection conn, boolean b) {
		/*try {
			if(conn!=null){
				if(conn.getAutoCommit()==b) return;
			}
		} 
		catch (Throwable e) {}*/
		
		
		try {
			
			if(conn!=null)conn.setAutoCommit(b);
        } 
        catch (Throwable e) {}
	}

	public static void setReadOnlyEL(Connection conn, boolean b) {
		try {
			if(conn!=null)conn.setReadOnly(b);
		} 
		catch (Throwable e) {}
	}

	public static void commitEL(Connection conn) {
		try {
			if(conn!=null)conn.commit();
		} 
		catch (Throwable e) {}
	}

	public static void setTransactionIsolationEL(Connection conn,int level) {
		try {
			if(conn!=null)conn.setTransactionIsolation(level);
		} 
		catch (Throwable e) {}
	}

	public static void closeEL(Statement stat) {
		if(stat!=null) {
            try {
                stat.close();
            } catch (Throwable t) {}
        }
	}

	public static void closeEL(ResultSet rs) {
		if(rs!=null) {
            try {
                rs.close();
            } catch (Throwable t) {}
        }
	}
	
	public static PreparedStatement getPreparedStatement(DatasourceConnection dc, SQL sql, boolean createGeneratedKeys) throws SQLException {
		if(createGeneratedKeys)	return dc.getConnection().prepareStatement(sql.getSQLString(),Statement.RETURN_GENERATED_KEYS);
    	return dc.getConnection().prepareStatement(sql.getSQLString());
	}

}
