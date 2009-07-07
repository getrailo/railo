package railo.runtime.interpreter;

import railo.runtime.exp.ExpressionException;

/**
 * 
 */
public final class InterpreterException extends ExpressionException {

	/* *
	 * constructor of the Exception
	 * @param e
	 * /
	public InterpreterException(Throwable e) {
		super(e);
	}*/

	/**
	 * constructor of the Exception
	 * @param message
	 * @param detail
	 */
	public InterpreterException(String message, String detail) {
		super(message, detail);
	}

	/**
	 * constructor of the Exception
	 * @param message
	 */
	public InterpreterException(String message) {
		super(message);
	}
	
}