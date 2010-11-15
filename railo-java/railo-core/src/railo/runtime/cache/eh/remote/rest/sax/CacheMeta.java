package railo.runtime.cache.eh.remote.rest.sax;

public class CacheMeta {

	private CacheConfiguration cacheConfiguration;
	private CacheStatistics cacheStatistics;

	public CacheMeta(CacheConfiguration cacheConfiguration, CacheStatistics cacheStatistics) {
		this.cacheConfiguration=cacheConfiguration;
		this.cacheStatistics=cacheStatistics;
	}

	/**
	 * @return the cacheConfiguration
	 */
	public CacheConfiguration getCacheConfiguration() {
		return cacheConfiguration;
	}

	/**
	 * @return the cacheStatistics
	 */
	public CacheStatistics getCacheStatistics() {
		return cacheStatistics;
	}
}
