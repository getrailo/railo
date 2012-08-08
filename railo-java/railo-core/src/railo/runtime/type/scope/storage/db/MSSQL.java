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

public class MSSQL extends SQLExecutorSupport {

	@Override
	public Query select(Config config, String cfid, String applicationName,
			DatasourceConnection dc, int type, Log log,
			boolean createTableIfNotExist) throws PageException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Config config, String cfid, String applicationName,
			DatasourceConnection dc, int type, Struct data, long timeSpan,
			Log log) throws PageException, SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Config config, String cfid, String appName,
			DatasourceConnection dc, int type, Log log)
			throws PageException, SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clean(Config config, DatasourceConnection dc, int type,
			StorageScopeEngine engine, DatasourceStorageScopeCleaner cleaner,
			StorageScopeListener listener, Log log) throws PageException,
			SQLException {
		// TODO Auto-generated method stub
		
	}

}
