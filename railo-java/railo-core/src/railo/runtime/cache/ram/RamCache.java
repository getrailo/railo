package railo.runtime.cache.ram;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.ReferenceMap;

import railo.commons.io.cache.CacheEntry;
import railo.runtime.cache.CacheSupport;
import railo.runtime.type.Struct;

public class RamCache extends CacheSupport {

	private Map entries= new ReferenceMap();
	private long missCount;
	private int hitCount;
	

	public void init(String cacheName, Struct arguments) throws IOException {
		//Caster.toBooleanValue(arguments.get("caseSensitive"),'');
	}
	
	/**
	 * @see railo.commons.io.cache.Cache#contains(java.lang.String)
	 */
	public boolean contains(String key) {
		return entries.containsKey(key);
	}

	
	

	public CacheEntry getQuiet(String key, CacheEntry defaultValue) {
		RamCacheEntry entry = (RamCacheEntry)entries.get(key);
		if(entry==null) {
			return defaultValue;
		}
		if(!valid(entry)) {
			entries.remove(key);
			return defaultValue;
		}
		return entry;
	}

	/**
	 * @see railo.commons.io.cache.Cache#getCacheEntry(java.lang.String, railo.commons.io.cache.CacheEntry)
	 */
	public CacheEntry getCacheEntry(String key, CacheEntry defaultValue) {
		RamCacheEntry ce = (RamCacheEntry) getQuiet(key, null);
		if(ce!=null) {
			hitCount++;
			return ce.read();
		}
		missCount++;
		return defaultValue;
	}

	/**
	 * @see railo.commons.io.cache.Cache#hitCount()
	 */
	public long hitCount() {
		return hitCount;
	}

	/**
	 * @see railo.commons.io.cache.Cache#missCount()
	 */
	public long missCount() {
		return missCount;
	}

	public List keys() {
		List list=new ArrayList();
		
		Iterator it = entries.entrySet().iterator();
		RamCacheEntry entry;
		while(it.hasNext()){
			entry=(RamCacheEntry) ((Map.Entry)it.next()).getValue();
			if(valid(entry))list.add(entry.getKey());
		}
		return list;
	}

	public void put(String key, Object value, Long idleTime, Long until) {
		RamCacheEntry entry=(RamCacheEntry) entries.get(key);
		if(entry==null)
			entries.put(key, new RamCacheEntry(key,value,idleTime,until));
		else
			entry.update(value);
	}

	public boolean remove(String key) {
		return entries.remove(key)!=null;
	}


}
