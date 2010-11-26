package railo.runtime.cache.eh;

import java.util.Date;

import net.sf.ehcache.Element;
import railo.commons.io.cache.CacheEntry;
import railo.runtime.cache.CacheUtil;
import railo.runtime.type.Struct;

public class EHCacheEntry implements CacheEntry {

	private Element element;

	public EHCacheEntry(Element element) {
		this.element=element;
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#created()
	 */
	public Date created() {
		return new Date(element.getCreationTime());
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#lastHit()
	 */
	public Date lastHit() {
		return new Date(element.getLastAccessTime());
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#lastModified()
	 */
	public Date lastModified() {
		long value = element.getLastUpdateTime();
		if(value==0)return created();
		return new Date(value); 
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#hitCount()
	 */
	public int hitCount() {
		return (int)element.getHitCount();
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#idleTimeSpan()
	 */
	public long idleTimeSpan() {
		return element.getTimeToIdle()*1000;
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#validUntil()
	 */
	public long liveTimeSpan() { 
		return element.getTimeToLive()*1000;
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#size()
	 */
	public long size() {
		return element.getSerializedSize();
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#getKey()
	 */
	public String getKey() {
		return (String) element.getKey();
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#getValue()
	 */
	public Object getValue() {
		return element.getObjectValue();
	}

	public void setElement(Element element) {
		this.element=element;
	}
	

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return CacheUtil.toString(this);
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#getCusomInfo()
	 */
	public Struct getCustomInfo() {
		Struct info=CacheUtil.getInfo(this);
		info.setEL("version", new Double(element.getVersion()));
		return info;
	}
}
