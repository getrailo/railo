package railo.runtime.type.scope.storage.db;

import java.sql.SQLException;

import railo.commons.io.log.Log;
import railo.runtime.config.Config;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.exp.PageException;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.scope.storage.StorageScopeEngine;
import railo.runtime.type.scope.storage.StorageScopeListener;
import railo.runtime.type.scope.storage.clean.DatasourceStorageScopeCleaner;

public interface SQLExecutor {

	/**
	 * does a select statement on the datasource to get data
	 * @param config Config of the current context
	 * @param cfid CFID of the current user
	 * @param applicationName name of the current application context
	 * @param dc Datasource Connection to use
	 * @param type storage type (Scope.SCOPE_CLIENT,Scope.SCOPE_SESSION)
	 * @param log 
	 * @param createTableIfNotExist do create the table if not existing
	 * @return data matching criteria
	 * @throws PageException
	 * @throws SQLException 
	 */
	public Query select(Config config, String cfid,String applicationName, DatasourceConnection dc, int type, Log log, boolean createTableIfNotExist) throws PageException,SQLException;

	/**
	 * updates the data in the datasource for a specific user (CFID), if the data not exist, a new record is created
	 * @param config Config of the current context
	 * @param cfid CFID of the current user
	 * @param applicationName name of the current application context
	 * @param dc Datasource Connection to use
	 * @param type storage type (Scope.SCOPE_CLIENT,Scope.SCOPE_SESSION)
	 * @param data data to store
	 * @param timeSpan timespan in millis 
	 * @param log 
	 * @throws PageException
	 * @throws SQLException
	 */
	public void update(Config config, String cfid,String applicationName, DatasourceConnection dc,int type,Struct data,long timeSpan, Log log) throws PageException,SQLException;

	/**
	 * deletes the data in the datasource for a specific user (CFID), if there is no data for this user nothing is happeing
	 * @param config Config of the current context
	 * @param cfid CFID of the current user
	 * @param applicationName name of the current application context
	 * @param dc Datasource Connection to use
	 * @param type storage type (Scope.SCOPE_CLIENT,Scope.SCOPE_SESSION)
	 * @param log 
	 * @throws PageException
	 * @throws SQLException
	 */
	public void delete(Config config, String cfid, String applicationName, DatasourceConnection dc, int type, Log log) throws PageException,SQLException;
 
	public void clean(Config config, DatasourceConnection dc, int type, StorageScopeEngine engine, DatasourceStorageScopeCleaner cleaner, StorageScopeListener listener, Log log) throws PageException,SQLException;   

}
