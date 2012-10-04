package railo.runtime.interpreter.ref.op;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;

/**
 * Dividie operation
 */
public final class BigDiv extends Big {

	/**
	 * constructor of the class
	 * @param left
	 * @param right
	 */
	public BigDiv(Ref left, Ref right) {
		super(left,right);
	}

	@Override
	public Object getValue(PageContext pc) throws PageException {
		return getLeft(pc).divide(getRight(pc)).toString();
	}
    

}
