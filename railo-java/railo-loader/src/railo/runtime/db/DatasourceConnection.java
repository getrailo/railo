package railo.runtime.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * a datasource and connection pair
 */
public interface DatasourceConnection {

    /**
     * @return Returns the connection.
     */
    public abstract Connection getConnection();

    /**
     * @return Returns the datasource.
     */
    public abstract DataSource getDatasource();

    /**
     * @return is timeout or not
     */
    public abstract boolean isTimeout();
    


	/**
	 * @return the password
	 */
	public String getPassword();

	/**
	 * @return the username
	 */
	public String getUsername() ;

	public boolean supportsGetGeneratedKeys();
	
	public PreparedStatement getPreparedStatement(SQL sql, boolean createGeneratedKeys) throws SQLException;
	public PreparedStatement getPreparedStatement(SQL sql, int resultSetType,int resultSetConcurrency) throws SQLException;
	
	public void close() throws SQLException;
	
	

	
}