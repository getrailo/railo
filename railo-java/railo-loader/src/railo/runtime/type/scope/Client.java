package railo.runtime.type.scope;

import railo.runtime.type.scope.storage.StorageScope;


/**
 * Interface of the scope client
 */
public interface Client extends Scope,SharedScope,StorageScope { 

    
	/**
	 * @return time when Client Scope last time is visited
	 */
	public abstract long lastVisit();

	/**
	 * @return all keys except the readpnly ones (cfid,cftoken,hitcount,lastvisit ...)
	 */
	public abstract String[] pureKeys();

}