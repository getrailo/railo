package railo.commons.io.cache;

public interface CacheKeyFilter extends CacheFilter {
	
	/**
	 * Tests if a specified key should be accepted.
	 * @param key key to check
	 * @return if and only if the Entry should be accepted; false otherwise.
	 */
	public boolean accept(String key);
	
}
