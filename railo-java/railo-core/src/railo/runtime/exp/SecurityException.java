package railo.runtime.exp;


/**
 * 
 */
public final class SecurityException extends PageExceptionImpl {

	/**
	 * Class Constuctor
	 * @param message error message
	 */
	public SecurityException(String message) {
		super(message,"security"); 
	}

	/**
	 * Class Constuctor
	 * @param message error message
	 * @param detail detailed error message
	 */
	public SecurityException(String message, String detail) {
		super(message,"security");
		setDetail(detail);
	}

}