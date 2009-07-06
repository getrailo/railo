package railo.commons.io.cache;

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
