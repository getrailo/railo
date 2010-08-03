package railo.runtime.interpreter.ref.op;

import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;

/**
 * Plus operation
 */
public final class BigPlus extends Big {

	/**
	 * constructor of the class
	 * @param left
	 * @param right
	 */
	public BigPlus(Ref left, Ref right) {
		super(left,right);
	}

	/**
	 * @see railo.runtime.interpreter.ref.Ref#getValue()
	 */
	public Object getValue() throws PageException {
		return getLeft().add(getRight()).toString();
	}
    

}
