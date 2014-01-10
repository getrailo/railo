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

	@Override
	public Date created() {
		return new Date(element.getCreationTime());
	}

	@Override
	public Date lastHit() {
		return new Date(element.getLastAccessTime());
	}

	@Override
	public Date lastModified() {
		long value = element.getLastUpdateTime();
		if(value==0)return created();
		return new Date(value); 
	}

	@Override
	public int hitCount() {
		return (int)element.getHitCount();
	}

	@Override
	public long idleTimeSpan() {
		return element.getTimeToIdle()*1000;
	}

	@Override
	public long liveTimeSpan() { 
		return element.getTimeToLive()*1000;
	}

	@Override
	public long size() {
		return element.getSerializedSize();
	}

	@Override
	public String getKey() {
		return (String) element.getKey();
	}

	@Override
	public Object getValue() {
		return element.getObjectValue();
	}

	public void setElement(Element element) {
		this.element=element;
	}
	

	@Override
	public String toString() {
		return CacheUtil.toString(this);
	}

	@Override
	public Struct getCustomInfo() {
		Struct info=CacheUtil.getInfo(this);
		info.setEL("version", new Double(element.getVersion()));
		return info;
	}
}
