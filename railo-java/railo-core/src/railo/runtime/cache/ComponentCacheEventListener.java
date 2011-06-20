package railo.runtime.cache;

import railo.commons.io.cache.CacheEntry;
import railo.commons.io.cache.CacheEventListener;
import railo.runtime.Component;
import railo.runtime.type.Collection;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.KeyImpl;

public class ComponentCacheEventListener implements CacheEventListener {

	private static final long serialVersionUID = 6271280246677734153L;
	private static final Collection.Key ON_EXPIRES = KeyImpl.getInstance("onExpires");
	private static final Collection.Key ON_PUT = KeyImpl.getInstance("onPut");
	private static final Collection.Key ON_REMOVE = KeyImpl.getInstance("onRemove");
	private Component cfc;

	public ComponentCacheEventListener(Component cfc) {
		this.cfc=cfc;
	}
	
	/**
	 * @see railo.commons.io.cache.CacheEventListener#onRemove(railo.commons.io.cache.CacheEntry)
	 */
	public void onRemove(CacheEntry entry) {
		call(ON_REMOVE,entry);
	}

	/**
	 * @see railo.commons.io.cache.CacheEventListener#onPut(railo.commons.io.cache.CacheEntry)
	 */
	public void onPut(CacheEntry entry) {
		call(ON_PUT,entry);
	}

	/**
	 * @see railo.commons.io.cache.CacheEventListener#onExpires(railo.commons.io.cache.CacheEntry)
	 */
	public void onExpires(CacheEntry entry) {
		call(ON_EXPIRES,entry);
	}

	private void call(Key methodName, CacheEntry entry) {
		//Struct data = entry.getCustomInfo();
		//cfc.callWithNamedValues(pc, methodName, data);
	}

	/**
	 * @see railo.commons.io.cache.CacheEventListener#duplicate()
	 */
	public CacheEventListener duplicate() {
		return new ComponentCacheEventListener((Component)cfc.duplicate(false));
	}

}
