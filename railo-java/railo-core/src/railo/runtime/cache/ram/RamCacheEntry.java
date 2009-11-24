package railo.runtime.cache.ram;

import java.util.Date;

import railo.commons.io.cache.CacheEntry;
import railo.extension.io.cache.CacheUtil;
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

	public RamCacheEntry(String key, Object value, Long idleTime, Long until) {
		this.key=key;
		this.value=value;
		this.idleTime=idleTime==null?Long.MIN_VALUE:idleTime.longValue();
		this.until=until==null?Long.MIN_VALUE:until.longValue();
		created=modifed=accessed=System.currentTimeMillis();
		hitCount=1;
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#created()
	 */
	public Date created() {
		return new Date(created);
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#getCustomInfo()
	 */
	public Struct getCustomInfo() {
		return CacheUtil.getInfo(this);
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#getKey()
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#getValue()
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#hitCount()
	 */
	public int hitCount() {
		return hitCount;
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#idleTimeSpan()
	 */
	public long idleTimeSpan() {
		return idleTime;
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#lastHit()
	 */
	public Date lastHit() {
		return new Date(accessed);
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#lastModified()
	 */
	public Date lastModified() {
		return new Date(modifed);
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#liveTimeSpan()
	 */
	public long liveTimeSpan() {
		return until;
	}

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
