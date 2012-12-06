package railo.runtime.type.scope.session;

import railo.commons.io.log.Log;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.scope.Session;
import railo.runtime.type.scope.storage.StorageScopeCache;

public final class SessionCache extends StorageScopeCache implements Session {
	
	private static final long serialVersionUID = -875719423763891692L;

	private SessionCache(PageContext pc,String cacheName, String appName,Struct sct) { 
		super(pc,cacheName,appName,"session",SCOPE_SESSION,sct);
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	private SessionCache(StorageScopeCache other,boolean deepCopy) {
		super(other,deepCopy);
	}
	
	@Override
	public Collection duplicate(boolean deepCopy) {
    	return new SessionCache(this,deepCopy);
	}
	
	/**
	 * load an new instance of the client datasource scope
	 * @param cacheName 
	 * @param appName
	 * @param pc
	 * @return client datasource scope
	 * @throws PageException
	 */
	public static Session getInstance(String cacheName, String appName, PageContext pc,Log log) throws PageException {
			Struct _sct = _loadData(pc, cacheName, appName,"session", log);
			//structOk=true;
			if(_sct==null) _sct=new StructImpl();
			
		return new SessionCache(pc,cacheName,appName,_sct);
	}
	
	public static boolean hasInstance(String cacheName, String appName, PageContext pc) {
		try {
			return _loadData(pc, cacheName, appName,"session", null)!=null;
		} 
		catch (PageException e) {
			return false;
		}
}
	

	public static Session getInstance(String cacheName, String appName, PageContext pc,Log log, Session defaultValue) {
		try {
			return getInstance(cacheName, appName, pc,log);
		}
		catch (PageException e) {}
		return defaultValue;
	}

}
