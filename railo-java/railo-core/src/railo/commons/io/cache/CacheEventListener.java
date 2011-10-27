package railo.commons.io.cache;

import java.io.Serializable;

/**
 * Ac CacheEventListener is registred to a cache implementing the interface CacheEvent, a CacheEventListener can listen to certain event happening in a cache
 */
public interface CacheEventListener extends Serializable {


	/**
	 * this method is invoked before a Cache Entry is removed from Cache
	 * @param entry entry that will be removed from Cache
	 */
	public void onRemove(CacheEntry entry);
	
	/**
	 * this method is invoked before a new Entry is putted to a cache (update and insert)
	 */
	public void onPut(CacheEntry entry);
	
	/**
	 * this method is invoked before a entry expires (lifetime and idletime)
	 */
	public void onExpires(CacheEntry entry);
	
	
	public CacheEventListener duplicate();
}
