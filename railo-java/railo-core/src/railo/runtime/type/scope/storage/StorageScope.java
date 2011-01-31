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
}
