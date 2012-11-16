package railo.runtime.db;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;

public interface DataSourceManager {

	/**
	 * return a database connection matching to datsource name 
	 * @param datasource datasource whished
	 * @param user username to datasource
	 * @param pass password to datasource
	 * @return return a Db Connectio9n Object
	 * @throws PageException 
	 * @deprecated use instead <code>getConnection(PageContext pc,DataSource ds, String user, String pass)</code>
	 */
	public DatasourceConnection getConnection(PageContext pc,String datasource,
			String user, String pass) throws PageException;
	
	/**
	 * return a database connection matching to datsource name 
	 * @param ds datasource whished
	 * @param user username to datasource
	 * @param pass password to datasource
	 * @return return a Db Connectio9n Object
	 * @throws PageException 
	 */
	public DatasourceConnection getConnection(PageContext pc,DataSource ds, String user, String pass) throws PageException;
	
	
	public abstract void releaseConnection(PageContext pc,DatasourceConnection dc) throws PageException;

	/**
	 * set state of transaction to begin
	 */
	public abstract void begin();

	/**
	 * set state of transaction to begin
	 * @param isolation isolation level of the transaction
	 */
	public abstract void begin(String isolation);

	/**
	 * set state of transaction to begin
	 * @param isolation isolation level of the transaction
	 */
	public abstract void begin(int isolation);

	/**
	 * rollback hanging transaction
	 * @throws DatabaseException
	 */
	public abstract void rollback() throws PageException;
	
	public abstract void savepoint() throws PageException;

	/**
	 * commit hanging transaction
	 * @throws DatabaseException
	 */
	public abstract void commit() throws PageException;

	/**
	 * @return return if manager is in autocommit mode or not
	 */
	public abstract boolean isAutoCommit();

	/**
	 * ends the manual commit state
	 */
	public abstract void end();

	public abstract void remove(String datasource);// FUTURE deprecated
	//FUTURE public abstract void remove(DataSource datasource);

	public abstract void release();

}