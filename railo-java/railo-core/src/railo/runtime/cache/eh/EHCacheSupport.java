package railo.runtime.cache.eh;

import java.util.List;

import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.event.RegisteredEventListeners;
import railo.commons.io.cache.Cache;
import railo.commons.io.cache.CacheEntry;
import railo.commons.io.cache.CacheEvent;
import railo.commons.io.cache.CacheEventListener;
import railo.runtime.cache.CacheSupport;
import railo.runtime.type.Struct;
import railo.runtime.type.util.KeyConstants;

public abstract class EHCacheSupport extends CacheSupport implements Cache,CacheEvent {

	@Override
	public void register(CacheEventListener listener) {
		//RegisteredEventListeners listeners=cache.getCacheEventNotificationService();
		//listeners.registerListener(new ExpiresCacheEventListener());
		
		
		net.sf.ehcache.Cache cache = getCache();
		RegisteredEventListeners service = cache.getCacheEventNotificationService();
		service.registerListener(new EHCacheEventListener(listener));
		
		
		//.getCacheEventListeners().add(new EHCacheEventListener(listener));
	}

	@Override
	public boolean contains(String key) {
		if(!getCache().isKeyInCache(key))return false;
		return getCache().get(key)!=null;
	}

	@Override
	public Struct getCustomInfo() {
		Struct info=super.getCustomInfo();
		// custom
		CacheConfiguration conf = getCache().getCacheConfiguration();
		info.setEL("disk_expiry_thread_interval", new Double(conf.getDiskExpiryThreadIntervalSeconds()));
		info.setEL("disk_spool_buffer_size", new Double(conf.getDiskSpoolBufferSizeMB()*1024*1024));
		info.setEL("max_elements_in_memory", new Double(conf.getMaxElementsInMemory()));
		info.setEL("max_elements_on_disk", new Double(conf.getMaxElementsOnDisk()));
		info.setEL("time_to_idle", new Double(conf.getTimeToIdleSeconds()));
		info.setEL("time_to_live", new Double(conf.getTimeToLiveSeconds()));
		info.setEL(KeyConstants._name, conf.getName());
		return info;
	}

	@Override
	public List keys() {
		return getCache().getKeysWithExpiryCheck();
	}
	
	@Override
	public void put(String key, Object value, Long idleTime, Long liveTime) {
		Boolean eternal = idleTime==null && liveTime==null?Boolean.TRUE:Boolean.FALSE;
		Integer idle = idleTime==null?null : new Integer( (int)(idleTime.longValue()/1000) );
		Integer live = liveTime==null?null : new Integer( (int)(liveTime.longValue()/1000) );
		getCache().put(new Element(key, value ,eternal, idle, live));
	}

	
	

	public CacheEntry getQuiet(String key, CacheEntry defaultValue){
		try {
			return new EHCacheEntry(getCache().getQuiet(key));
		} catch (Throwable t) {
			return defaultValue;
		}
	}
	
	public CacheEntry getQuiet(String key) {
		return new EHCacheEntry(getCache().getQuiet(key));
	}

	protected abstract net.sf.ehcache.Cache getCache();
	
	
}
