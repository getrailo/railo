package coldfusion.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public interface DataSource extends javax.sql.DataSource {
	
	public void remove() throws SQLException;
	
	@Override
	public Connection getConnection() throws SQLException;

	@Override
	public Connection getConnection(String user,String pass) throws SQLException;

	public void setDataSourceDef(DataSourceDef dsDef);

	public DataSourceDef getDataSourceDef();

	@Override
	public PrintWriter getLogWriter() throws SQLException;

	@Override
	public int getLoginTimeout() throws SQLException;

	@Override
	public void setLogWriter(PrintWriter pw) throws SQLException;

	@Override
	public void setLoginTimeout(int timeout) throws SQLException;

	public boolean isDisabled();

}
