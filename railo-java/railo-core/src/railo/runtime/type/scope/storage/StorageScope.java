package railo.runtime.type.scope.storage;

import railo.runtime.config.Config;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.SharedScope;


/**
 * scope that can be stored, in a storage
 */
public interface StorageScope extends SharedScope {
	
	public static Collection.Key CFID=KeyImpl.getInstance("cfid");
	public static Collection.Key CFTOKEN=KeyImpl.getInstance("cftoken");
	public static Collection.Key URLTOKEN=KeyImpl.getInstance("urltoken");
	public static Collection.Key LASTVISIT=KeyImpl.getInstance("lastvisit");
	public static Collection.Key HITCOUNT=KeyImpl.getInstance("hitcount");
	public static Collection.Key TIMECREATED=KeyImpl.getInstance("timecreated");
	public static Collection.Key SESSION_ID=KeyImpl.getInstance("sessionid");
	
	/**
	 * @return time when the Scope last time was visited
	 */
	public abstract long lastVisit();
	
	public abstract String getStorageType();
	
	public long getLastAccess();
	
	public void touch();
	
	public boolean isExpired();
	
	public long getTimeSpan();

	/**
	 * store content on persistent layer
	 */
	public void store(Config config);
	
	/**
	 * remove stored data from persistent layer
	 */
	public void unstore(Config config);
	

	/**
	 * sets the name of the storage used, this is not the storage type!
	 * @param storage
	 */
	public void setStorage(String storage);
	/**
	 * return the name of the storage used, this is not the storage type!
	 * @return
	 */
	public String getStorage();
	

	/**
	 * Specifies the time, in seconds, between client requests before the servlet container will invalidate this session. A negative time indicates the session should never timeout.
	 * @param interval - An integer specifying the number of seconds
	 */
	public void setMaxInactiveInterval(int interval);
	
	/**
	 * Returns the maximum time interval, in seconds, that the servlet container will keep this session open between client accesses. After this interval, the servlet container will invalidate the session. The maximum time interval can be set with the setMaxInactiveInterval method. A negative time indicates the session should never timeout.
	 * @return an integer specifying the number of seconds this session remains open between client requests
	 */
	public int getMaxInactiveInterval();
}
