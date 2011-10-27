package railo.commons.io.cache;

/**
 * A optional interface a Cache can implement. You can register CacheEventListener to a Cache, this CacheEventListener are invoked when a cerain event occur.
 */
public interface CacheEvent {

	/**
	 * allows to register a CacheEventListener for one or more certain events
	 * @param event
	 */
	public void register(CacheEventListener listener);
}
