
package railo.runtime.converter;


/**
 * Exception throwed by a Converter
 */
public final class ConverterException extends Exception {

	private static final long serialVersionUID = -5591914619316366638L;

	/**
	 * constructor of the Exception
	 * @param message
	 */
	public ConverterException(String message) {
		super(message);
	}
}