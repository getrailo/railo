package railo.commons.io.cache;

import java.io.IOException;
import java.util.List;

import railo.runtime.type.Struct;

public interface Cache {
	
	// FUTURE remove
	public static final String DEFAULT_CACHE_NAME = "default789575785";
	
	/**
	 * initialize the cache
	 * @param arguments configuration arguments
	 * @throws CacheException 
	 */
	public void init(String cacheName,Struct arguments) throws IOException;
	
	//FUTURE public void init(Config config,String cacheName,Struct arguments) throws IOException;
	
	/**
	 * return cache entry that match the key, throws a CacheException when entry does not exists or is stale
	 * @param key key of the cache entry to get
	 * @return cache entry
	 * @throws CacheException
	 */
	public CacheEntry getCacheEntry(String key) throws IOException;
	
	/**
	 * return value that match the key, throws a CacheException when entry does not exists or is stale
	 * @param key key of the value to get
	 * @return value
	 * @throws CacheException
	 */
	public Object getValue(String key) throws IOException;
	
	/**
	 * return cache entry that match the key or the defaultValue when entry does not exist
	 * @param key key of the cache entry to get
	 * @return cache entry
	 */
	public CacheEntry getCacheEntry(String key,CacheEntry defaultValue);
	
	/**
	 * return value that match the key or the defaultValue when entry does not exist
	 * @param key key of the value to get
	 * @return value
	 */
	public Object getValue(String key,Object defaultValue);
	
	/**
	 * puts a cache entry to the cache, overwrite existing entries that already exists inside the cache with the same key
	 * @param value
	 */
	public void put(String key, Object value,Long idleTime,Long until);//FUTURE throws IOException;
	
	/**
	 * check if there is a entry inside the cache that match the given key
	 * @param key
	 * @return contains a value that match this key
	 */
	public boolean contains(String key);
	

	/**
	 * remove entry that match this key
	 * @param key
	 * @return returns if there was a removal
	 */
	public boolean remove(String key);//FUTURE throws IOException;
	
	/**
	 * remove all entries that match the given filter
	 * @param filter 
	 * @return returns the count of the removal or -1 if this information is not available
	 */
	public int remove(CacheKeyFilter filter);//FUTURE throws IOException;
	
	/**
	 * remove all entries that match the given filter
	 * @param filter 
	 * @return returns the count of the removal or -1 if this information is not available
	 */
	public int remove(CacheEntryFilter filter);//FUTURE throws IOException;


	/**
	 * 
	 * Returns a List of the keys contained in this cache. 
	 * The set is NOT backed by the cache, so changes to the cache are NOT reflected in the set, and vice-versa. 
	 * @return a set of the keys contained in this cache.
	 */
	public List keys();//FUTURE throws IOException;
	
	/**
	 * 
	 * Returns a List of the keys contained in this cache that match the given filter. 
	 * The set is NOT backed by the cache, so changes to the cache are NOT reflected in the set, and vice-versa. 
	 * @param filter 
	 * @return a set of the keys contained in this cache.
	 */
	public List keys(CacheKeyFilter filter);//FUTURE throws IOException;
	
	/**
	 * 
	 * Returns a List of the keys contained in this cache that match the given filter. 
	 * The set is NOT backed by the cache, so changes to the cache are NOT reflected in the set, and vice-versa. 
	 * @param filter 
	 * @return a set of the keys contained in this cache.
	 */
	public List keys(CacheEntryFilter filter);//FUTURE throws IOException; // FUTURE List<CacheEntry>
	
	/**
	 * Returns a List of values containing in this cache. 
	 * The set is NOT backed by the cache, so changes to the cache are NOT reflected in the set, and vice-versa. 
	 * @return a set of the entries contained in this cache.
	 */
	public List values();//FUTURE throws IOException; // FUTURE List<CacheEntry>
	
	/**
	 * Returns a list of values containing in this cache that match the given filter.
	 * The set is NOT backed by the cache, so changes to the cache are NOT reflected in the set, and vice-versa. 
	 * @return a set of the entries contained in this cache.
	 */
	public List values(CacheKeyFilter filter);//FUTURE throws IOException; // FUTURE List<CacheEntry>
	
	/**
	 * Returns a list of values containing in this cache that match the given filter.
	 * The set is NOT backed by the cache, so changes to the cache are NOT reflected in the set, and vice-versa. 
	 * @return a set of the entries contained in this cache.
	 */
	public List values(CacheEntryFilter filter);//FUTURE throws IOException;  List<CacheEntry>
	
	/**
	 * Returns a List of entries containing in this cache Each element in the returned list is a CacheEntry. 
	 * The set is NOT backed by the cache, so changes to the cache are NOT reflected in the set, and vice-versa. 
	 * @return a set of the entries contained in this cache.
	 */
	public List entries();// FUTURE public List<CacheEntry> entries();
	
	/**
	 * Returns a list of entries containing in this cache that match the given filter.
	 * Each element in the returned set is a CacheEntry. 
	 * The set is NOT backed by the cache, so changes to the cache are NOT reflected in the set, and vice-versa. 
	 * @return a set of the entries contained in this cache.
	 */
	public List entries(CacheKeyFilter filter); // FUTURE List<CacheEntry>
	
	/**
	 * Returns a list of entries containing in this cache that match the given filter.
	 * Each element in the returned set is a CacheEntry. 
	 * The set is NOT backed by the cache, so changes to the cache are NOT reflected in the set, and vice-versa. 
	 * @return a set of the entries contained in this cache.
	 */
	public List entries(CacheEntryFilter filter); // FUTURE List<CacheEntry>


	/**
	 * how many time was the cache accessed?
	 * this information is optional and depends on the implementation,
	 * when information is not available -1 is returned
	 * @return access count
	 */
	public long hitCount();

	/**
	 * how many time was the cache accessed for a record that does not exist?
	 * this information is optional and depends on the implementation,
	 * when information is not available -1 is returned
	 * @return access count
	 */
	public long missCount();

	/**
	 * get all information data available for this cache
	 */
	public Struct getCustomInfo();

	/**
	 * removes the cache coplitly
	 */
	// FUTURE public void remove() throws IOException; or better clear()
	
	// FUTURE public void verify() throws IOException;

}
