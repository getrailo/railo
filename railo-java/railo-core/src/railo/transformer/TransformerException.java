package railo.transformer;

import railo.commons.lang.ClassException;
import railo.runtime.exp.TemplateException;

public final class TransformerException extends TemplateException {

	private static final long serialVersionUID = 6750275378601018748L;

	private Position pos;

	public TransformerException(String message, Position pos) {
		super(message);
		this.pos=pos;
	}

	public TransformerException(ClassException cause, Position start) {
		this(cause.getMessage(),start);
		initCause(cause);
	}

	public Position getPosition() {
		return pos;
	}

}
