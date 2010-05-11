package railo.runtime.orm;


import railo.runtime.exp.ApplicationException;

public class ORMException extends ApplicationException {

	/**
	 * Constructor of the class
	 * @param message
	 */
	public ORMException(String message) {
		super(message);
	}

	/**
	 * Constructor of the class
	 * @param message
	 * @param detail
	 */
	public ORMException(String message, String detail) {
		super(message, detail);
	}

	public ORMException(Throwable t) {
		super(t.getMessage());
	}

}
