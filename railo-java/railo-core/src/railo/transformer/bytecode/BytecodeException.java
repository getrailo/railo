package railo.transformer.bytecode;

import railo.commons.lang.ClassException;
import railo.runtime.exp.TemplateException;

public final class BytecodeException extends TemplateException {

	private Position pos;

	public BytecodeException(String message, Position pos) {
		super(message);
		this.pos=pos;
	}

	public BytecodeException(ClassException cause, Position start) {
		this(cause.getMessage(),start);
		initCause(cause);
	}

	public Position getPosition() {
		return pos;
	}

}
