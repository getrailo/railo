package railo.runtime.type.scope.client;

import railo.commons.io.log.Log;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.scope.ClientPlus;
import railo.runtime.type.scope.storage.StorageScopeCache;

public final class ClientCache extends StorageScopeCache implements ClientPlus {
	
	private static final long serialVersionUID = -875719423763891692L;
	private static final boolean expiresControlFromOutside=false;

	private ClientCache(PageContext pc,String cacheName, String appName,Struct sct) { 
		super(pc,cacheName,appName,"client",SCOPE_CLIENT,sct,expiresControlFromOutside);
	}

	/**
	 * Constructor of the class, clone existing
	 * @param other
	 */
	private ClientCache(StorageScopeCache other,boolean deepCopy) {
		super(other,deepCopy);
	}
	

	
	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
    	return new ClientCache(this,deepCopy);
	}
	
	/**
	 * load an new instance of the client datasource scope
	 * @param cacheName 
	 * @param appName
	 * @param pc
	 * @param log 
	 * @return client datasource scope
	 * @throws PageException
	 */
	public static ClientPlus getInstance(String cacheName, String appName, PageContext pc, Log log) throws PageException {
			Struct _sct = _loadData(pc, cacheName, appName,"client",log);
			//structOk=true;
			if(_sct==null) _sct=new StructImpl();
			
		return new ClientCache(pc,cacheName,appName,_sct);
	}
	

	public static ClientPlus getInstance(String cacheName, String appName, PageContext pc, Log log,ClientPlus defaultValue) {
		try {
			return getInstance(cacheName, appName, pc,log);
		}
		catch (PageException e) {}
		return defaultValue;
	}

}
