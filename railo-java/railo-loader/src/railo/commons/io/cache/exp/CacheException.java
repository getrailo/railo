package railo.commons.io.cache.exp;

import java.io.IOException;

/**
 * Exceptin throwed by Cache or CacheEntry
 */
public class CacheException extends IOException {

	private static final long serialVersionUID = -7937763383640628704L;

	/**
	 * Constructor of the class
	 * @param message
	 */
	public CacheException(String message){
		super(message);
	}
}
