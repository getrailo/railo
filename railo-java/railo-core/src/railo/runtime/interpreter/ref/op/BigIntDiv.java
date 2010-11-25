package railo.runtime.interpreter.ref.op;

import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;

/**
 * Dividie operation
 */
public final class BigIntDiv extends Big {

	/**
	 * constructor of the class
	 * @param left
	 * @param right
	 */
	public BigIntDiv(Ref left, Ref right) {
		super(left,right);
	}

	/**
	 * @see railo.runtime.interpreter.ref.Ref#getValue()
	 */
	public Object getValue() throws PageException {
		return getLeft().toBigInteger().divide(getRight().toBigInteger()).toString();
	}
}
