package railo.runtime.cache.eh.remote.soap;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.charset.Charset;
import java.util.Date;

import railo.commons.io.IOUtil;
import railo.commons.io.cache.CacheEntry;
import railo.runtime.cache.CacheUtil;
import railo.runtime.type.Struct;

public class SoapCacheEntry implements CacheEntry {

	private Element element;

	public SoapCacheEntry(Element element) {
		this.element=element;
		
	}


	@Override
	public Date created() {
		return new Date(element.getExpirationDate().longValue()-element.getTimeToLiveSeconds().longValue());
	}

	@Override
	public Date lastHit() {
		return new Date(0);
		// TODO return new Date(element.getLastAccessTime());
	}

	@Override
	public Date lastModified() {
		return new Date(0);
		// TODO long value = element.getLastUpdateTime();
		// TODO if(value==0)return created();
		// TODO return new Date(value); 
	}

	@Override
	public int hitCount() {
		return 0;
		// TODO return (int)element.getHitCount();
	}

	@Override
	public long idleTimeSpan() {
		return element.getTimeToIdleSeconds().intValue()*1000;
	}

	@Override
	public long liveTimeSpan() { 
		return element.getTimeToLiveSeconds().intValue()*1000;
	}

	@Override
	public long size() {
		return element.getValue().length;
	}

	@Override
	public String getKey() {
		return (String) element.getKey();
	}

	@Override
	public Object getValue() {
		try{
		ByteArrayInputStream bais = new ByteArrayInputStream(element.getValue());
		if("application/x-java-serialized-object".equals(element.getMimeType())){
			ObjectInputStream ois=new ObjectInputStream(bais);
    		return ois.readObject();
	    }
	    // other
	    return IOUtil.toString(bais,(Charset)null);
		}
		catch(Throwable t){
			return null;
		}
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
		// TODO info.setEL("version", new Double(element.getVersion()));
		return info;
	}

}
