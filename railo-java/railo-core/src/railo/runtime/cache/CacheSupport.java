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
import railo.runtime.type.Struct;

public abstract class CacheSupport implements Cache {

	@Override
	public List<String> keys(CacheKeyFilter filter) throws IOException {
		List<String> keys = keys();
		List<String> list=new ArrayList<String>();
		Iterator<String> it = keys.iterator();
		String key;
		while(it.hasNext()){
			key= it.next();
			if(filter.accept(key))list.add(key);
		}
		return list;
	}
	
	@Override
	public List<CacheEntry> keys(CacheEntryFilter filter) throws IOException {
		List<String> keys = keys();
		List<CacheEntry> list=new ArrayList<CacheEntry>();
		Iterator<String> it = keys.iterator();
		String key;
		CacheEntry entry;
		while(it.hasNext()){
			key=it.next();
			entry=getQuiet(key,null);
			if(filter.accept(entry))list.add(entry);
		}
		return list;
	}
	
	@Override
	public List<CacheEntry> entries() throws IOException {
		List<String> keys = keys();
		List<CacheEntry> list=new ArrayList<CacheEntry>();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			list.add(getQuiet(it.next(),null));
		}
		return list;
	}
	
	@Override
	public List<CacheEntry> entries(CacheKeyFilter filter) throws IOException {
		List<String> keys = keys();
		List<CacheEntry> list=new ArrayList<CacheEntry>();
		Iterator<String> it = keys.iterator();
		String key;
		while(it.hasNext()){
			key=it.next();
			if(filter.accept(key))list.add(getQuiet(key,null));
		}
		return list;
	}
	
	@Override
	public List<CacheEntry> entries(CacheEntryFilter filter) throws IOException {
		List<String> keys = keys();
		List<CacheEntry> list=new ArrayList<CacheEntry>();
		Iterator<String> it = keys.iterator();
		CacheEntry entry;
		while(it.hasNext()){
			entry=getQuiet(it.next(),null);
			if(filter.accept(entry))list.add(entry);
		}
		return list;
	}

	// there was the wrong generic type defined in the older interface, because of that we do not define a generic type at all here, just to be sure
	@Override
	public List values() throws IOException {
		List<String> keys = keys();
		List<Object> list=new ArrayList<Object>();
		Iterator<String> it = keys.iterator();
		String key;
		while(it.hasNext()){
			key=it.next();
			list.add(getQuiet(key,null).getValue());
		}
		return list;
	}

	// there was the wrong generic type defined in the older interface, because of that we do not define a generic type at all here, just to be sure
	@Override
	public List values(CacheEntryFilter filter) throws IOException {
		List<String> keys = keys();
		List<Object> list=new ArrayList<Object>();
		Iterator<String> it = keys.iterator();
		String key;
		CacheEntry entry;
		while(it.hasNext()){
			key=it.next();
			entry=getQuiet(key,null);
			if(filter.accept(entry))list.add(entry.getValue());
		}
		return list;
	}

	// there was the wrong generic type defined in the older interface, because of that we do not define a generic type at all here, just to be sure
	@Override
	public List values(CacheKeyFilter filter) throws IOException {
		List<String> keys = keys();
		List<Object> list=new ArrayList<Object>();
		Iterator<String> it = keys.iterator();
		String key;
		while(it.hasNext()){
			key=it.next();
			if(filter.accept(key))list.add(getQuiet(key,null).getValue());
		}
		return list;
	}
	
	@Override
	public int remove(CacheEntryFilter filter) throws IOException {
		List<String> keys = keys();
		int count=0;
		Iterator<String> it = keys.iterator();
		String key;
		CacheEntry entry;
		while(it.hasNext()){
			key=it.next();
			entry=getQuiet(key,null);
			if(filter==null || filter.accept(entry)){
				remove(key);
				count++;
			}
		}
		return count;
	}
	

	@Override
	public int remove(CacheKeyFilter filter) throws IOException {
		List<String> keys = keys();
		int count=0;
		Iterator<String> it = keys.iterator();
		String key;
		while(it.hasNext()){
			key=it.next();
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
	

	@Override
	public Object getValue(String key) throws IOException {
		return getCacheEntry(key).getValue();
	}

	@Override
	public Object getValue(String key, Object defaultValue) {
		CacheEntry entry = getCacheEntry(key,null);
		if(entry==null) return defaultValue;
		return entry.getValue();
	} 
	
	protected static boolean valid(CacheEntry entry) {
		long now = System.currentTimeMillis();
		if(entry.liveTimeSpan()>0 && entry.liveTimeSpan()+entry.lastModified().getTime()<now){
			return false;
		}
		if(entry.idleTimeSpan()>0 && entry.idleTimeSpan()+entry.lastHit().getTime()<now){
			return false;
		}
		return true;
	}
	
	@Override
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
