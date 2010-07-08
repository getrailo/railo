package coldfusion.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public interface DataSource extends javax.sql.DataSource {
	
	public void remove() throws SQLException;

	/**
	 * @see javax.sql.DataSource#getConnection()
	 */
	public Connection getConnection() throws SQLException;

	/**
	 * @see javax.sql.DataSource#getConnection(java.lang.String, java.lang.String)
	 */
	public Connection getConnection(String user,String pass) throws SQLException;

	public void setDataSourceDef(DataSourceDef dsDef);

	public DataSourceDef getDataSourceDef();

	public PrintWriter getLogWriter() throws SQLException;

	public int getLoginTimeout() throws SQLException;

	public void setLogWriter(PrintWriter pw) throws SQLException;

	public void setLoginTimeout(int timeout) throws SQLException;

	public boolean isDisabled();

}
