package railo.runtime.type.scope.storage;

import java.sql.SQLException;

import railo.commons.io.log.Log;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigImpl;
import railo.runtime.db.DataSource;
import railo.runtime.db.DatasourceConnection;
import railo.runtime.db.DatasourceConnectionPool;
import railo.runtime.debug.DebuggerPro;
import railo.runtime.debug.DebuggerUtil;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.scope.ScopeContext;
import railo.runtime.type.scope.storage.db.SQLExecutionFactory;
import railo.runtime.type.scope.storage.db.SQLExecutor;
import railo.runtime.type.util.KeyConstants;

/**
 * client scope that store it's data in a datasource
 */
public abstract class StorageScopeDatasource extends StorageScopeImpl {

	private static final long serialVersionUID = 239179599401918216L;

	public static final String PREFIX = "cf";
	
	private String datasourceName;

	private String appName;

	private String cfid;
	
	
	/**
	 * Constructor of the class
	 * @param pc
	 * @param name
	 * @param sct
	 * @param b 
	 */
	protected StorageScopeDatasource(PageContext pc,String datasourceName, String strType,int type,Struct sct) { 
		super(
				sct,
				doNowIfNull(pc,Caster.toDate(sct.get(TIMECREATED,null),false,pc.getTimeZone(),null)),
				doNowIfNull(pc,Caster.toDate(sct.get(LASTVISIT,null),false,pc.getTimeZone(),null)),
				-1, 
				type==SCOPE_CLIENT?Caster.toIntValue(sct.get(HITCOUNT,"1"),1):0,
				strType,type);

		this.datasourceName=datasourceName; 
		appName=pc.getApplicationContext().getName();
		cfid=pc.getCFID();
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	protected StorageScopeDatasource(StorageScopeDatasource other,boolean deepCopy) {
		super(other,deepCopy);
		this.datasourceName=other.datasourceName;
	}
	
	private static DateTime doNowIfNull(PageContext pc,DateTime dt) {
		if(dt==null)return new DateTimeImpl(pc.getConfig());
		return dt;
	}
	
	
	
	
	protected static Struct _loadData(PageContext pc, String datasourceName,String strType,int type, Log log, boolean mxStyle) throws PageException	{
		ConfigImpl config = (ConfigImpl)pc.getConfig();
		DatasourceConnectionPool pool = config.getDatasourceConnectionPool();
		DatasourceConnection dc=pool.getDatasourceConnection(pc,((PageContextImpl)pc).getDataSource(datasourceName),null,null);
		SQLExecutor executor=SQLExecutionFactory.getInstance(dc);
		
		
		Query query;
		
		try {
			if(!dc.getDatasource().isStorage()) 
				throw new ApplicationException("storage usage for this datasource is disabled, you can enable this in the railo administrator.");
			query = executor.select(pc.getConfig(),pc.getCFID(),pc.getApplicationContext().getName(), dc, type,log, true);
		} 
		catch (SQLException se) {
			throw Caster.toPageException(se);
		}
	    finally {
	    	if(dc!=null) pool.releaseDatasourceConnection(dc);
	    }
	    
	    if(query!=null && pc.getConfig().debug()) {
	    	boolean debugUsage=DebuggerUtil.debugQueryUsage(pc,query);
	    	((DebuggerPro)pc.getDebugger()).addQuery(debugUsage?query:null,datasourceName,"",query.getSql(),query.getRecordcount(),pc.getCurrentPageSource(),query.getExecutionTime());
	    }
	    boolean _isNew = query.getRecordcount()==0;
	    
	    if(_isNew) {
	    	ScopeContext.info(log,"create new "+strType+" scope for "+pc.getApplicationContext().getName()+"/"+pc.getCFID()+" in datasource ["+datasourceName+"]");
			return null;
	    }
	    String str=Caster.toString(query.get(KeyConstants._data));
	    if(mxStyle) return null;
	    Struct s = (Struct)pc.evaluate(str);
	    ScopeContext.info(log,"load existing data from ["+datasourceName+"."+PREFIX+"_"+strType+"_data] to create "+strType+" scope for "+pc.getApplicationContext().getName()+"/"+pc.getCFID());
		
	    return s;
	}

	@Override
	public void touchAfterRequest(PageContext pc) {
		setTimeSpan(pc);
		super.touchAfterRequest(pc); 
		
		store(pc.getConfig());
	}
	
	public void store(Config config) {
		//if(!super.hasContent()) return;
		
		DatasourceConnection dc = null;
		ConfigImpl ci = (ConfigImpl)config;
		DatasourceConnectionPool pool = ci.getDatasourceConnectionPool();
		Log log=((ConfigImpl)config).getScopeLogger();
		try {
			PageContext pc = ThreadLocalPageContext.get();// FUTURE change method interface
			DataSource ds;
			if(pc!=null) ds=((PageContextImpl)pc).getDataSource(datasourceName);
			else ds=config.getDataSource(datasourceName);
			dc=pool.getDatasourceConnection(null,ds,null,null);
			SQLExecutor executor=SQLExecutionFactory.getInstance(dc);
			executor.update(config, cfid,appName, dc, getType(), sct,getTimeSpan(),log);
		} 
		catch (Throwable t) {
			ScopeContext.error(log, t);
		}
		finally {
			if(dc!=null) pool.releaseDatasourceConnection(dc);
		}
	}
	
	public void unstore(Config config) {
		ConfigImpl ci=(ConfigImpl) config;
		DatasourceConnection dc = null;
		
		
		DatasourceConnectionPool pool = ci.getDatasourceConnectionPool();
		Log log=((ConfigImpl)config).getScopeLogger();
		try {
			PageContext pc = ThreadLocalPageContext.get();// FUTURE change method interface
			DataSource ds;
			if(pc!=null) ds=((PageContextImpl)pc).getDataSource(datasourceName);
			else ds=config.getDataSource(datasourceName);
			dc=pool.getDatasourceConnection(null,ds,null,null);
			SQLExecutor executor=SQLExecutionFactory.getInstance(dc);
			executor.delete(config, cfid,appName, dc, getType(),log);
		} 
		catch (Throwable t) {
			ScopeContext.error(log, t);
		}
		finally {
			if(dc!=null) pool.releaseDatasourceConnection(dc);
		}
	}
	
	

	
	
	
	
	
	

	@Override
	public void touchBeforeRequest(PageContext pc) {
		setTimeSpan(pc);
		super.touchBeforeRequest(pc);
	}
	
	@Override
	public String getStorageType() {
		return "Datasource";
	}

	/**
	 * @return the datasourceName
	 */
	public String getDatasourceName() {
		return datasourceName;
	}
}
