package railo.runtime.cfx;


/**
 * Custom Tag Exception
 */
public final class CFXTagException extends Exception {
	
	/**
	 * Constructor of the Exception
	 * @param message
	 */
	public CFXTagException(String message) {
		super(message);
	}

	/**
	 * Constructor of the Exception
	 * @param e
	 */
	public CFXTagException(Throwable e) {
		super(e.getClass().getName()+":"+e.getMessage());
	}
}