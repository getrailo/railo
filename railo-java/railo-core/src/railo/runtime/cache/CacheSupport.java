package railo.runtime.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import railo.commons.io.cache.Cache;
import railo.commons.io.cache.CacheEntry;
import railo.commons.io.cache.CacheEntryFilter;
import railo.commons.io.cache.CacheKeyFilter;
import railo.commons.io.cache.exp.CacheException;
import railo.extension.io.cache.CacheUtil;
import railo.runtime.type.Struct;

public abstract class CacheSupport implements Cache {

	/**
	 * @see railo.commons.io.cache.Cache#keys(railo.commons.io.cache.CacheKeyFilter)
	 */
	public List keys(CacheKeyFilter filter) {
		List keys = keys();
		List list=new ArrayList();
		Iterator it = keys.iterator();
		String key;
		while(it.hasNext()){
			key=(String) it.next();
			if(filter.accept(key))list.add(key);
		}
		return list;
	}
	
	/**
	 * @see railo.commons.io.cache.Cache#keys(railo.commons.io.cache.CacheEntryFilter)
	 */
	public List keys(CacheEntryFilter filter) {
		List keys = keys();
		List list=new ArrayList();
		Iterator it = keys.iterator();
		String key;
		CacheEntry entry;
		while(it.hasNext()){
			key=(String) it.next();
			entry=getQuiet(key,null);
			if(filter.accept(entry))list.add(key);
		}
		return list;
	}
	
	/**
	 * @see railo.commons.io.cache.Cache#entries()
	 */
	public List entries() {
		List keys = keys();
		List list=new ArrayList();
		Iterator it = keys.iterator();
		String key;
		while(it.hasNext()){
			key=(String) it.next();
			list.add(getQuiet(key,null));
		}
		return list;
	}
	
	/**
	 * @see railo.commons.io.cache.Cache#entries(railo.commons.io.cache.CacheKeyFilter)
	 */
	public List entries(CacheKeyFilter filter) {
		List keys = keys();
		List list=new ArrayList();
		Iterator it = keys.iterator();
		String key;
		while(it.hasNext()){
			key=(String) it.next();
			if(filter.accept(key))list.add(getQuiet(key,null));
		}
		return list;
	}
	
	/**
	 * @see railo.commons.io.cache.Cache#entries(railo.commons.io.cache.CacheEntryFilter)
	 */
	public List entries(CacheEntryFilter filter) {
		List keys = keys();
		List list=new ArrayList();
		Iterator it = keys.iterator();
		String key;
		CacheEntry entry;
		while(it.hasNext()){
			key=(String) it.next();
			entry=getQuiet(key,null);
			if(filter.accept(entry))list.add(entry);
		}
		return list;
	}
	
	/**
	 * @see railo.commons.io.cache.Cache#values()
	 */
	public List values() {
		List keys = keys();
		List list=new ArrayList();
		Iterator it = keys.iterator();
		String key;
		while(it.hasNext()){
			key=(String) it.next();
			list.add(getQuiet(key,null).getValue());
		}
		return list;
	}
	

	/**
	 * @see railo.commons.io.cache.Cache#values(railo.commons.io.cache.CacheEntryFilter)
	 */
	public List values(CacheEntryFilter filter) {
		List keys = keys();
		List list=new ArrayList();
		Iterator it = keys.iterator();
		String key;
		CacheEntry entry;
		while(it.hasNext()){
			key=(String) it.next();
			entry=getQuiet(key,null);
			if(filter.accept(entry))list.add(entry.getValue());
		}
		return list;
	}
	
	/**
	 * @see railo.commons.io.cache.Cache#values(railo.commons.io.cache.CacheKeyFilter)
	 */
	public List values(CacheKeyFilter filter) {
		List keys = keys();
		List list=new ArrayList();
		Iterator it = keys.iterator();
		String key;
		while(it.hasNext()){
			key=(String) it.next();
			if(filter.accept(key))list.add(getQuiet(key,null).getValue());
		}
		return list;
	}
	
	/**
	 * @see railo.commons.io.cache.Cache#remove(railo.commons.io.cache.CacheEntryFilter)
	 */
	public int remove(CacheEntryFilter filter) {
		List keys = keys();
		int count=0;
		Iterator it = keys.iterator();
		String key;
		CacheEntry entry;
		while(it.hasNext()){
			key=(String) it.next();
			entry=getQuiet(key,null);
			if(filter==null || filter.accept(entry)){
				remove(key);
				count++;
			}
		}
		return count;
	}
	

	/**
	 * @see railo.commons.io.cache.Cache#remove(railo.commons.io.cache.CacheKeyFilter)
	 */
	public int remove(CacheKeyFilter filter) {
		List keys = keys();
		int count=0;
		Iterator it = keys.iterator();
		String key;
		while(it.hasNext()){
			key=(String) it.next();
			if(filter==null || filter.accept(key)){
				remove(key);
				count++;
			}
		}
		return count;
	}
	
	public Struct getCustomInfo() {
		return CacheUtil.getInfo(this);
	}
	

	/**
	 * @see railo.commons.io.cache.Cache#getValue(java.lang.String)
	 */
	public Object getValue(String key) throws IOException {
		return getCacheEntry(key).getValue();
	}

	/**
	 * @see railo.commons.io.cache.Cache#getValue(java.lang.String, java.lang.Object)
	 */
	public Object getValue(String key, Object defaultValue) {
		CacheEntry entry = getCacheEntry(key,null);
		if(entry==null) return defaultValue;
		return entry.getValue();
	} 
	
	protected boolean valid(CacheEntry entry) {
		long now = System.currentTimeMillis();
		if(entry.liveTimeSpan()>0 && entry.liveTimeSpan()+entry.created().getTime()<now){
			return false;
		}
		if(entry.idleTimeSpan()>0 && entry.idleTimeSpan()+entry.lastHit().getTime()<now){
			return false;
		}
		return true;
	}
	
	/**
	 * @see railo.commons.io.cache.Cache#getCacheEntry(java.lang.String)
	 */
	public CacheEntry getCacheEntry(String key) throws IOException {
		CacheEntry entry = getCacheEntry(key, null);
		if(entry==null) throw new CacheException("there is no valid cache entry with key ["+key+"]");
		return entry;
	}
	
	public CacheEntry getQuiet(String key) throws IOException {
		CacheEntry entry = getQuiet(key, null);
		if(entry==null) throw new CacheException("there is no valid cache entry with key ["+key+"]");
		return entry;
	}

	public abstract CacheEntry getQuiet(String key, CacheEntry defaultValue);
	

}
