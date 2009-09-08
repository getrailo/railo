package railo.commons.io.cache;

import java.util.Date;

import railo.runtime.type.Struct;


/**
 * interface for a entry inside the cache, this interface is read-only
 */
public interface CacheEntry {
	
	/**
	 * when was the entry accessed last time.
	 * this information is optional and depends on the implementation,
	 * when information is not available -1 is returned
	 * @return time in milliseconds since 1/1/1970 GMT
	 */
	public Date lastHit();
	
	/**
	 * when was the entry last time modified.
	 * this information is optional and depends on the implementation,
	 * when information is not available -1 is returned
	 * @return time offset in milliseconds since 1/1/1970 GMT
	 */
	public Date lastModified();
	
	/**
	 * when was the entry created.
	 * this information is optional and depends on the implementation,
	 * when information is not available -1 is returned
	 * @return time offset in milliseconds since 1/1/1970 GMT
	 */
	public Date created();
	
	/**
	 * how many time was the entry accessed?
	 * this information is optional and depends on the implementation,
	 * when information is not available -1 is returned
	 * @return access count
	 */
	public int hitCount();
	
	
	/**
	 * @return the key associated with this entry
	 */
	public String getKey();
	
	/**
	 * @return the value associated with this entry
	 */
	public Object getValue();
	
	/**
	 * the size of the object
	 * @return size of the object
	 */
	public long size();
	
	/**
	 * define time until the entry is valid
	 * @return time offset in milliseconds since 1/1/1970 GMT or Long.MIN_VALUE if value is not defined
	 */
	public long liveTimeSpan();
	
	/**
	 * time in milliseconds after which the object is flushed from the cache if it is not accessed during that time.
	 * @return time milliseconds  since 1/1/1970 GMT or Long.MIN_VALUE if value is not defined
	 */
	public long idleTimeSpan();

	/**
	 * get all information data available for this entry
	 */
	public Struct getCustomInfo();
}