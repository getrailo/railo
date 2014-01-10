package railo.runtime.type.scope.storage.clean;

import java.sql.SQLException;

import railo.commons.io.log.Log;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.DatasourceConnectionPool;
import railo.runtime.exp.PageException;
import railo.runtime.type.scope.storage.StorageScopeEngine;
import railo.runtime.type.scope.storage.StorageScopeListener;
import railo.runtime.type.scope.storage.db.SQLExecutionFactory;
import railo.runtime.type.scope.storage.db.SQLExecutor;

public class DatasourceStorageScopeCleaner extends StorageScopeCleanerSupport {
	
	//private String strType;
	
	public DatasourceStorageScopeCleaner(int type,StorageScopeListener listener) {
		super(type,listener,INTERVALL_HOUR);
		//this.strType=VariableInterpreter.scopeInt2String(type);
	}
	
	public void init(StorageScopeEngine engine) {
		super.init(engine);
	}

	protected void _clean() {
		ConfigWeb config = engine.getFactory().getConfig();
		DataSource[] datasources = config.getDataSources();
		for(int i=0;i<datasources.length;i++){
			
			if((datasources[i]).isStorage()) {
				try {
					clean(config,datasources[i]);
				} catch (Throwable t) {
					error(t);
				}
			}
		}
	}

	private void clean(ConfigWeb config, DataSource dataSource) throws PageException, SQLException	{
		ConfigWebImpl cwi=(ConfigWebImpl) config;
		DatasourceConnection dc=null;
		
		DatasourceConnectionPool pool = cwi.getDatasourceConnectionPool();
		try {
			dc=pool.getDatasourceConnection(null,dataSource,null,null);
			Log log=((ConfigImpl)config).getScopeLogger();
			SQLExecutor executor=SQLExecutionFactory.getInstance(dc);
			executor.clean(config, dc, type, engine,this, listener, log);
		}
	    finally {
	    	if(dc!=null) pool.releaseDatasourceConnection(dc);
	    }
	}
}
