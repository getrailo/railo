package railo.runtime.type.scope.storage.db;

import java.sql.SQLException;

import railo.commons.io.log.Log;
import railo.runtime.config.Config;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.exp.PageException;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;

public class MSSQL extends SQLExecutorSupport {

	@Override
	public Query select(Config config, String cfid, String applicationName,
			DatasourceConnection dc, String type, Log log,
			boolean createTableIfNotExist) throws PageException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Config config, String cfid, String applicationName,
			DatasourceConnection dc, String type, Struct data, long timeSpan,
			Log log) throws PageException, SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Config config, String cfid, String appName,
			DatasourceConnection dc, String typeAsString, Log log)
			throws PageException, SQLException {
		// TODO Auto-generated method stub

	}

}
