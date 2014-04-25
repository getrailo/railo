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

	@Override
	public Date created() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Struct getCustomInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hitCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long idleTimeSpan() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Date lastHit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date lastModified() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long liveTimeSpan() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long size() {
		// TODO Auto-generated method stub
		return 0;
	}

}
