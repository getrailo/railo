package railo.runtime.type.scope.storage;

import java.io.IOException;

import railo.commons.io.cache.Cache;
import railo.commons.io.log.Log;
import railo.runtime.PageContext;
import railo.runtime.cache.CacheConnection;
import railo.runtime.config.Config;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.cache.Util;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;
import railo.runtime.type.scope.ScopeContext;

/**
 * client scope that store it's data in a datasource
 */
public abstract class StorageScopeCache extends StorageScopeImpl {

	private static final long serialVersionUID = 6234854552927320080L;

	public static final long SAVE_EXPIRES_OFFSET = 60*60*1000;

	//private static ScriptConverter serializer=new ScriptConverter();
	//boolean structOk;
	
	private final String cacheName;
	private final String appName;
	private final String cfid;


	
	
	/**
	 * Constructor of the class
	 * @param pc
	 * @param name
	 * @param sct
	 * @param b 
	 */
	protected StorageScopeCache(PageContext pc,String cacheName, String appName,String strType,int type,Struct sct) { 
		// !!! do not store the pagecontext or config object, this object is Serializable !!!
		super(
				sct,
				doNowIfNull(pc.getConfig(),Caster.toDate(sct.get(TIMECREATED,null),false,pc.getTimeZone(),null)),
				doNowIfNull(pc.getConfig(),Caster.toDate(sct.get(LASTVISIT,null),false,pc.getTimeZone(),null)),
				-1, 
				type==SCOPE_CLIENT?Caster.toIntValue(sct.get(HITCOUNT,"1"),1):1
				,strType,type);
		
		//this.isNew=isNew;
		this.appName=appName;
		this.cacheName=cacheName;
		this.cfid=pc.getCFID();
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	protected StorageScopeCache(StorageScopeCache other,boolean deepCopy) {
		super(other,deepCopy);
		
		this.appName=other.appName;
		this.cacheName=other.cacheName;
		this.cfid=other.cfid;
	}
	
	private static DateTime doNowIfNull(Config config,DateTime dt) {
		if(dt==null)return new DateTimeImpl(config);
		return dt;
	}
	
	@Override
	public void touchAfterRequest(PageContext pc) {
		setTimeSpan(pc);
		super.touchAfterRequest(pc);
		//if(super.hasContent()) 
			store(pc.getConfig());
	}

	@Override
	public String getStorageType() {
		return "Cache";
	}

	@Override
	public void touchBeforeRequest(PageContext pc) {
		setTimeSpan(pc);
		super.touchBeforeRequest(pc);
	}
	
	protected static Struct _loadData(PageContext pc, String cacheName, String appName, String strType, Log log) throws PageException	{
		Cache cache = getCache(pc.getConfig(),cacheName);
		String key=getKey(pc.getCFID(),appName,strType);
		
		Struct s = (Struct) cache.getValue(key,null);
		
		if(s!=null)
			ScopeContext.info(log,"load existing data from  cache ["+cacheName+"] to create "+strType+" scope for "+pc.getApplicationContext().getName()+"/"+pc.getCFID());
		else
			ScopeContext.info(log,"create new "+strType+" scope for "+pc.getApplicationContext().getName()+"/"+pc.getCFID()+" in cache ["+cacheName+"]");
		
		return s;
	}

	public void store(Config config) {
		try {
			Cache cache = getCache(config, cacheName);
			/*if(cache instanceof CacheEvent) {
				CacheEvent ce=(CacheEvent) cache;
				ce.register(new SessionEndCacheEvent());
			}*/
			String key=getKey(cfid, appName, getTypeAsString());
			cache.put(key, sct,new Long(getTimeSpan()), null);
		} 
		catch (Exception pe) {}
	}
	
	public void unstore(Config config) {
		try {
			Cache cache = getCache(config, cacheName);
			String key=getKey(cfid, appName, getTypeAsString());
			cache.remove(key);
		} 
		catch (Exception pe) {}
	}
	

	private static Cache getCache(Config config, String cacheName) throws PageException {
		try {
			CacheConnection cc = Util.getCacheConnection(config,cacheName);
			if(!cc.isStorage()) 
				throw new ApplicationException("storage usage for this cache is disabled, you can enable this in the railo administrator.");
			return cc.getInstance(config); 
		} catch (IOException e) {
			throw Caster.toPageException(e);
		}
	}
	

	public static String getKey(String cfid, String appName, String type) {
		return new StringBuilder("railo-storage:").append(type).append(":").append(cfid).append(":").append(appName).toString();
	}
	
	/*private void setTimeSpan(PageContext pc) {
		ApplicationContext ac=(ApplicationContext) pc.getApplicationContext();
		timespan = (getType()==SCOPE_CLIENT?ac.getClientTimeout().getMillis():ac.getSessionTimeout().getMillis())+(expiresControlFromOutside?SAVE_EXPIRES_OFFSET:0L);
		
	}*/
}
