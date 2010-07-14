
package railo.runtime.converter;

import railo.runtime.op.Caster;


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
		super(Caster.toClassName(e)+":"+e.getMessage());
        setStackTrace(e.getStackTrace());
	}
}