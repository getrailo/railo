package railo.commons.io.cache;

public interface CacheEntryFilter extends CacheFilter {
	
	/**
	 * Tests if a specified entry should be accepted.
	 * @param entry entry to check
	 * @return if and only if the Entry should be accepted; false otherwise.
	 */
	public boolean accept(CacheEntry entry);
}
