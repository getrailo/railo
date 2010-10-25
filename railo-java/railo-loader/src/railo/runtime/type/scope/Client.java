package railo.runtime.type.scope;

import railo.runtime.type.Scope;

/**
 * Interface of the scope client
 */
public interface Client extends Scope { 

    
	/**
	 * @return time when Client Scope last time is visited
	 */
	public abstract long lastVisit();

	/**
	 * @return all keys except the readpnly ones (cfid,cftoken,hitcount,lastvisit ...)
	 */
	public abstract String[] pureKeys();

}