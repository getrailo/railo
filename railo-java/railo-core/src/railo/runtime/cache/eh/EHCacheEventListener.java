package railo.runtime.cache.eh;

import java.io.Serializable;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import railo.commons.io.cache.CacheEventListener;


public class EHCacheEventListener implements net.sf.ehcache.event.CacheEventListener,Serializable {

	private static final long serialVersionUID = 5931737203770901097L;

	private CacheEventListener listener;

	public EHCacheEventListener(CacheEventListener listener) {
		this.listener=listener;
	}
	

	/**
	 * @see net.sf.ehcache.event.CacheEventListener#notifyElementExpired(net.sf.ehcache.Ehcache, net.sf.ehcache.Element)
	 */
	public void notifyElementExpired(Ehcache cache, Element element) {
		listener.onExpires(new EHCacheEntry(element));
	}

	/**
	 * @see net.sf.ehcache.event.CacheEventListener#notifyElementPut(net.sf.ehcache.Ehcache, net.sf.ehcache.Element)
	 */
	public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
		listener.onPut(new EHCacheEntry(element));
	}

	/**
	 * @see net.sf.ehcache.event.CacheEventListener#notifyElementRemoved(net.sf.ehcache.Ehcache, net.sf.ehcache.Element)
	 */
	public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
		listener.onRemove(new EHCacheEntry(element));
	}
	
	
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyElementEvicted(Ehcache arg0, Element arg1) {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
		listener.onPut(new EHCacheEntry(element));
	}

	@Override
	public void notifyRemoveAll(Ehcache arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone(){
		return new EHCacheEventListener(listener.duplicate()); 
	}
}
