package railo.runtime.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import railo.runtime.PageContext;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.DatasourceConnectionImpl;
import railo.runtime.db.SQL;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.op.Caster;

public class ORMDatasourceConnection implements DatasourceConnection {

	private DataSource datasource;
	private Connection connection;
	private Boolean supportsGetGeneratedKeys;

	public ORMDatasourceConnection(PageContext pc, ORMSession session) {
		datasource=session.getDataSource();
		// this should never happen
		if(datasource==null) {
			try {
				datasource=ORMUtil.getDataSource(pc);
			}
			catch (PageException pe) {
				throw new PageRuntimeException(pe);
			}
		}
		connection=new ORMConnection(pc,session);
	}

	public Connection getConnection() {
		// TODO Auto-generated method stub
		return connection;
	}

	@Override
	public DataSource getDatasource() {
		return datasource;
	}

	@Override
	public String getPassword() {
		return datasource.getPassword();
	}

	@Override
	public String getUsername() {
		return datasource.getUsername();
	}

	@Override
	public boolean isTimeout() {
		return false;
	}
	


	@Override
	public boolean equals(Object obj) {
		if(this==obj) return true;
		if(!(obj instanceof ORMDatasourceConnection)) return false;
		return DatasourceConnectionImpl.equals(this, (DatasourceConnection) obj);
	}

	public boolean supportsGetGeneratedKeys() {
		if(supportsGetGeneratedKeys==null){
			try {
				supportsGetGeneratedKeys=Caster.toBoolean(getConnection().getMetaData().supportsGetGeneratedKeys());
			} catch (Throwable t) {
				return false;
			}
		}
		return supportsGetGeneratedKeys.booleanValue();
	}

	public PreparedStatement getPreparedStatement(SQL sql, boolean createGeneratedKeys, boolean allowCaching) throws SQLException {
		if(createGeneratedKeys)	return getConnection().prepareStatement(sql.getSQLString(),Statement.RETURN_GENERATED_KEYS);
		return getConnection().prepareStatement(sql.getSQLString());
	}

	@Override
	public PreparedStatement getPreparedStatement(SQL sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return getConnection().prepareStatement(sql.getSQLString(),resultSetType,resultSetConcurrency);
	}

	@Override
	public void close() throws SQLException {
		getConnection().close();
	}

}
