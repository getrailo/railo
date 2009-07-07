package railo.runtime.text.csv;

import railo.runtime.exp.ApplicationException;

/**
 * CSV Parser Exception
 */
public final class CSVParserException extends ApplicationException {

	/**
	 * @param message
	 */
	public CSVParserException(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param detail
	 */
	public CSVParserException(String message, String detail) {
		super(message, detail);
	}	
}