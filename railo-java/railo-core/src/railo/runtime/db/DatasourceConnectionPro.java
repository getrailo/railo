package railo.runtime.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

// FUTURE move to interface DatasourceConnection and delete this interface
public interface DatasourceConnectionPro extends DatasourceConnection {
	
	public boolean supportsGetGeneratedKeys();
	
	public PreparedStatement getPreparedStatement(SQL sql, boolean createGeneratedKeys, boolean allowCaching) throws SQLException;
	public PreparedStatement getPreparedStatement(SQL sql, int resultSetType,int resultSetConcurrency) throws SQLException;
	
	public void close() throws SQLException;
}
