package railo.commons.io.cache.exp;

import java.io.IOException;

/**
 * Exceptin throwed by Cache or CacheEntry
 */
public class CacheException extends IOException {
	
	/**
	 * Constructor of the class
	 * @param message
	 */
	public CacheException(String message){
		super(message);
	}
}
