package railo.runtime.cache.util;

import railo.commons.io.cache.CacheKeyFilter;

/**
 * accept everything
 */
public class CacheKeyFilterAll implements CacheKeyFilter {

	private static CacheKeyFilterAll instance=new CacheKeyFilterAll();

	/**
	 * @see railo.commons.io.cache.CacheKeyFilter#accept(java.lang.String)
	 */
	public boolean accept(String key) {
		return true;
	}

	/**
	 * @see railo.commons.io.cache.CacheFilter#toPattern()
	 */
	public String toPattern() {
		return "*";
	}

	public static CacheKeyFilterAll getInstance() {
		return instance;
	}

}
