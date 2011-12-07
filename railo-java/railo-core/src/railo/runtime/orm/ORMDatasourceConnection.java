package railo.runtime.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import railo.runtime.PageContext;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.DatasourceConnectionImpl;
import railo.runtime.db.DatasourceConnectionPro;
import railo.runtime.db.SQL;
import railo.runtime.op.Caster;

public class ORMDatasourceConnection implements DatasourceConnectionPro {

	private DataSource datasource;
	private Connection connection;
	private Boolean supportsGetGeneratedKeys;

	public ORMDatasourceConnection(PageContext pc, ORMSession session) {
		datasource=session.getEngine().getDataSource();
		connection=new ORMConnection(pc,session);
	}

	public Connection getConnection() {
		// TODO Auto-generated method stub
		return connection;
	}

	/**
	 * @see railo.runtime.db.DatasourceConnection#getDatasource()
	 */
	public DataSource getDatasource() {
		return datasource;
	}

	/**
	 * @see railo.runtime.db.DatasourceConnection#getPassword()
	 */
	public String getPassword() {
		return datasource.getPassword();
	}

	/**
	 * @see railo.runtime.db.DatasourceConnection#getUsername()
	 */
	public String getUsername() {
		return datasource.getUsername();
	}

	/**
	 * @see railo.runtime.db.DatasourceConnection#isTimeout()
	 */
	public boolean isTimeout() {
		return false;
	}
	


	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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

	public PreparedStatement getPreparedStatement(SQL sql, boolean createGeneratedKeys) throws SQLException {
		if(createGeneratedKeys)	return getConnection().prepareStatement(sql.getSQLString(),Statement.RETURN_GENERATED_KEYS);
		else return getConnection().prepareStatement(sql.getSQLString());
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
