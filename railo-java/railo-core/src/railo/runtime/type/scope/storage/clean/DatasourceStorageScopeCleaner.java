package railo.runtime.type.scope.storage.clean;

import java.sql.Types;

import railo.runtime.config.ConfigWeb;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.db.DataSource;
import railo.runtime.db.DataSourceImpl;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.DatasourceConnectionPool;
import railo.runtime.db.SQL;
import railo.runtime.db.SQLImpl;
import railo.runtime.db.SQLItem;
import railo.runtime.db.SQLItemImpl;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.scope.storage.StorageScopeDatasource;
import railo.runtime.type.scope.storage.StorageScopeEngine;
import railo.runtime.type.scope.storage.StorageScopeListener;

public class DatasourceStorageScopeCleaner extends StorageScopeCleanerSupport {
	public static final Collection.Key CFID = KeyImpl.getInstance("cfid");
	public static final Collection.Key NAME = KeyImpl.getInstance("name");
	
	//private String strType;
	
	public DatasourceStorageScopeCleaner(int type,StorageScopeListener listener) {
		super(type,listener);
		//this.strType=VariableInterpreter.scopeInt2String(type);
	}
	
	public void init(StorageScopeEngine engine) {
		super.init(engine);
	}

	public void clean() {
		ConfigWeb config = engine.getFactory().getConfig();
		DataSource[] datasources = config.getDataSources();
		for(int i=0;i<datasources.length;i++){
			
			if(((DataSourceImpl)datasources[i]).isStorage()) {
				try {
					clean(config,datasources[i]);
				} catch (PageException e) {
					error(e);
				}
			}
		}
	}

	private void clean(ConfigWeb config, DataSource dataSource) throws PageException	{
		ConfigWebImpl cwi=(ConfigWebImpl) config;
		DatasourceConnection dc=null;
		Query query=null;
	    
		//  	executeUpdate(config,dc.getConnection(),"insert into "+PREFIX+"_"+getTypeAsString()+"_data (expires,data,cfid,name) values(?,?,?,?)",false);
		   
	    // select
	    SQL sqlSelect=new SQLImpl("select cfid,name from "+StorageScopeDatasource.PREFIX+"_"+strType+"_data where expires<=?"
						,new SQLItem[]{
			 		new SQLItemImpl(System.currentTimeMillis(),Types.VARCHAR)
				});
	    //print.o(sqlSelect);
		
		DatasourceConnectionPool pool = cwi.getDatasourceConnectionPool();
		try {
			dc=pool.getDatasourceConnection(null,dataSource,null,null);
			query = new QueryImpl(dc,sqlSelect,-1,-1,-1,"query");
			int recordcount=query.getRecordcount();
			//print.o("recordcount:"+recordcount);
			
			String cfid,name;
			for(int row=1;row<=recordcount;row++){
				cfid=Caster.toString(query.getAt(CFID, row, null),null);
				name=Caster.toString(query.getAt(NAME, row, null),null);
				
				if(listener!=null)listener.doEnd(engine, this,name, cfid);
				
				
				info("remove "+strType+"/"+name+"/"+cfid+" from datasource "+dataSource.getName());
				engine.remove(type,name,cfid);
				SQLImpl sql = new SQLImpl("delete from "+StorageScopeDatasource.PREFIX+"_"+strType+"_data where cfid=? and name=?",new SQLItem[]{
						new SQLItemImpl(cfid,Types.VARCHAR),
						new SQLItemImpl(name,Types.VARCHAR)
						});
				new QueryImpl(dc,sql,-1,-1,-1,"query");
				
				
				
			}
			
			
		}
	    finally {
	    	if(dc!=null) pool.releaseDatasourceConnection(dc);
	    }
	    
	    
	    
	    
	    
	    //long expires=Caster.toLongValue(query.get(StorageScopeDatasource.EXPIRES));
	    
	    
	}
}
