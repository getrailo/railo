package railo.runtime.services;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import coldfusion.sql.DataSource;
import coldfusion.sql.DataSourceDef;

public class DataSourceImpl implements DataSource {

	private railo.runtime.db.DataSource ds;

	public DataSourceImpl(railo.runtime.db.DataSource ds) {
		this.ds=ds;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return getConnection(ds.getUsername(), ds.getPassword());
	}

	@Override
	public Connection getConnection(String user, String pass)
			throws SQLException {
		try {
			PageContext pc = ThreadLocalPageContext.get();
			return pc.getDataSourceManager().getConnection(pc,ds.getName(),user, pass).getConnection();
		} catch (PageException e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public DataSourceDef getDataSourceDef() {
		return new DatSourceDefImpl(ds);
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isDisabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void remove() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDataSourceDef(DataSourceDef dsDef) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLogWriter(PrintWriter pw) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLoginTimeout(int timeout) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

}
