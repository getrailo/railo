package railo.runtime.cache.eh;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

public class ExpiresCacheEventListener implements CacheEventListener {

	

	public void notifyElementExpired(Ehcache cache, Element el) {
		//print.o("expired:"+el.getKey());
	}

	public void notifyElementRemoved(Ehcache cache, Element el)throws CacheException {

		//print.o("removed:"+el.getKey());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone(){
		return new ExpiresCacheEventListener();
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void notifyElementEvicted(Ehcache arg0, Element el) {
		// TODO Auto-generated method stub

		//print.o("Evicted:"+el.getKey());
	}

	public void notifyElementPut(Ehcache arg0, Element el)
			throws CacheException {
		// TODO Auto-generated method stub
		//print.o("put:"+el.getKey());
		
	}

	public void notifyElementUpdated(Ehcache arg0, Element el)
			throws CacheException {
		// TODO Auto-generated method stub
		//print.o("updated:"+el.getKey());
		
	}

	public void notifyRemoveAll(Ehcache arg0) {
		// TODO Auto-generated method stub
		//print.o("removeAll:");
		
	}

}
