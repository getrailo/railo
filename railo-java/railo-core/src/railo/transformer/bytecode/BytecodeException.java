package railo.transformer.bytecode;

import railo.commons.lang.ClassException;
import railo.runtime.exp.TemplateException;

public final class BytecodeException extends TemplateException {

	private int line;

	public BytecodeException(String message, int line) {
		super(message);
		this.line=line;
	}

	public BytecodeException(ClassException cause, int line) {
		this(cause.getMessage(),line);
		initCause(cause);
	}

	/**
	 * @return the line
	 */
	public int getLineAsInt() {
		return line;
	}

	public int getColumnAsInt() {
		return 0;
	}

}
