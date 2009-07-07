package railo.runtime.interpreter.ref.op;

import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;

/**
 * Minus operation
 */
public final class BigMinus extends Big {

	/**
	 * constructor of the class
	 * @param left
	 * @param right
	 */
	public BigMinus(Ref left, Ref right) {
		super(left,right);
	}

	/**
	 * @see railo.runtime.interpreter.ref.Ref#getValue()
	 */
	public Object getValue() throws PageException {
		return getLeft().subtract(getRight()).toString();
	}
    

}
