package railo.runtime.orm;

import java.sql.Connection;

import railo.runtime.PageContext;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.DatasourceConnectionImpl;

public class ORMDatasourceConnection implements DatasourceConnection {

	private DataSource datasource;
	private Connection connection;

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

}
