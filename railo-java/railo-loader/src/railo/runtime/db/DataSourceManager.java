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
	 */
	public abstract DatasourceConnection getConnection(PageContext pc,String datasource,
			String user, String pass) throws PageException;
	
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

	public abstract void remove(String datasource);

	// FUTURE public abstract void release();

}