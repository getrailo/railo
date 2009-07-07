package railo.runtime.search;



/**
 * Search Exception
 */
public final class SearchException extends Exception {
	/**
     * constructor o the class
	 * @param message
	 */
	public SearchException(String message) {
		super(message);
	}

    /**
     * @param e
     */
    public SearchException(Exception e) {
        super(e.getMessage());
        setStackTrace(e.getStackTrace());
    }
}