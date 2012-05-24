package railo.runtime.interpreter.ref.op;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;

/**
 * Minus operation
 */
public final class BigMulti extends Big {

	/**
	 * constructor of the class
	 * @param left
	 * @param right
	 */
	public BigMulti(Ref left, Ref right) {
		super(left,right);
	}

	@Override
	public Object getValue(PageContext pc) throws PageException {
		return getLeft(pc).multiply(getRight(pc)).toString();
	}
    

}
