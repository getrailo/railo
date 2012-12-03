package railo.runtime.interpreter.ref.op;

import railo.runtime.PageContext;
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

	@Override
	public Object getValue(PageContext pc) throws PageException {
		return getLeft(pc).add(getRight(pc)).toString();
	}
    

}
