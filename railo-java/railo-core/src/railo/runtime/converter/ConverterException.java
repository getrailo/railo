
package railo.runtime.converter;


/**
 * Exception throwed by a Converter
 */
public final class ConverterException extends Exception {
    
	/**
	 * constructor of the Exception
	 * @param message
	 */
	public ConverterException(String message) {
		super(message);
	}

	/**
	 * constructor takes a Exception
	 * @param e exception to encapsulate
	 */
	public ConverterException(Exception e) {
		super(e.getClass().getName()+":"+e.getMessage());
        setStackTrace(e.getStackTrace());
	}
}