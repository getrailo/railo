package railo.runtime.cache.ram;

import java.util.Date;

import railo.commons.io.cache.CacheEntry;
import railo.runtime.cache.CacheUtil;
import railo.runtime.type.Struct;

public class RamCacheEntry implements CacheEntry {

	private String key;
	private Object value;
	private long idleTime;
	private long until;
	private long created;
	private long modifed;
	private long accessed;
	private int hitCount;

	public RamCacheEntry(String key, Object value, long idleTime, long until) {
		this.key=key;
		this.value=value;
		this.idleTime=idleTime;
		this.until=until;
		created=modifed=accessed=System.currentTimeMillis();
		hitCount=1;
	}

	@Override
	public Date created() {
		return new Date(created);
	}

	@Override
	public Struct getCustomInfo() {
		return CacheUtil.getInfo(this);
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
	public int hitCount() {
		return hitCount;
	}

	@Override
	public long idleTimeSpan() {
		return idleTime;
	}

	@Override
	public Date lastHit() {
		return new Date(accessed);
	}

	@Override
	public Date lastModified() {
		return new Date(modifed);
	}

	@Override
	public long liveTimeSpan() {
		return until;
	}

	@Override
	public long size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void update(Object value) {
		this.value=value;
		modifed=accessed=System.currentTimeMillis();
		hitCount++;
	}

	public RamCacheEntry read() {
		accessed=System.currentTimeMillis();
		hitCount++;
		return this;
	}
}
