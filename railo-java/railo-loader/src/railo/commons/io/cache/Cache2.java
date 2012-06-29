package railo.commons.io.cache;

import java.io.IOException;

import railo.commons.io.cache.exp.CacheException;

public interface Cache2 extends Cache {
	
	/**
	 * clears the complete Cache
	 * @throws IOException
	 */
	public void clear() throws IOException;
	
	/**
	 * verifies the cache, throws a exception if something is wrong with the cache
	 * @throws CacheException
	 */
	public void verify() throws CacheException;
}
