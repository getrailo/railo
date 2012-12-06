package railo.runtime.cache.eh.remote.rest;

import java.util.Date;

import railo.commons.io.cache.CacheEntry;
import railo.runtime.type.Struct;

public class RESTCacheEntry implements CacheEntry {

	private String key;
	private Object value;

	public RESTCacheEntry(String key, Object value) {
		this.key=key;
		this.value=value;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public Object getValue() {
		return value;
	}

	public Date created() {
		// TODO Auto-generated method stub
		return null;
	}

	public Struct getCustomInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public int hitCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long idleTimeSpan() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Date lastHit() {
		// TODO Auto-generated method stub
		return null;
	}

	public Date lastModified() {
		// TODO Auto-generated method stub
		return null;
	}

	public long liveTimeSpan() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long size() {
		// TODO Auto-generated method stub
		return 0;
	}

}
