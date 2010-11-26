package railo.runtime.cache.eh.remote.soap;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Date;

import railo.commons.io.cache.CacheEntry;
import railo.loader.util.Util;
import railo.runtime.cache.CacheUtil;
import railo.runtime.type.Struct;

public class SoapCacheEntry implements CacheEntry {

	private Element element;

	public SoapCacheEntry(Element element) {
		this.element=element;
		
	}


	/**
	 * @see railo.commons.io.cache.CacheEntry#created()
	 */
	public Date created() {
		return new Date(element.getExpirationDate().longValue()-element.getTimeToLiveSeconds().longValue());
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#lastHit()
	 */
	public Date lastHit() {
		return new Date(0);
		// TODO return new Date(element.getLastAccessTime());
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#lastModified()
	 */
	public Date lastModified() {
		return new Date(0);
		// TODO long value = element.getLastUpdateTime();
		// TODO if(value==0)return created();
		// TODO return new Date(value); 
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#hitCount()
	 */
	public int hitCount() {
		return 0;
		// TODO return (int)element.getHitCount();
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#idleTimeSpan()
	 */
	public long idleTimeSpan() {
		return element.getTimeToIdleSeconds().intValue()*1000;
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#validUntil()
	 */
	public long liveTimeSpan() { 
		return element.getTimeToLiveSeconds().intValue()*1000;
	}

	/**
	 * @see railo.commons.io.cache.CacheEntry#size()
	 */
	public long size() {
		return element.getValue().length;
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
		try{
		ByteArrayInputStream bais = new ByteArrayInputStream(element.getValue());
		if("application/x-java-serialized-object".equals(element.getMimeType())){
			ObjectInputStream ois=new ObjectInputStream(bais);
    		return ois.readObject();
	    }
	    // other
	    return Util.toString(bais);
		}
		catch(Throwable t){
			return null;
		}
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
		// TODO info.setEL("version", new Double(element.getVersion()));
		return info;
	}

}
