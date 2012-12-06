package railo.runtime.cache.util;

import railo.commons.io.cache.CacheKeyFilter;

/**
 * accept everything
 */
public class CacheKeyFilterAll implements CacheKeyFilter {

	private static CacheKeyFilterAll instance=new CacheKeyFilterAll();

	@Override
	public boolean accept(String key) {
		return true;
	}

	@Override
	public String toPattern() {
		return "*";
	}

	public static CacheKeyFilterAll getInstance() {
		return instance;
	}

}
