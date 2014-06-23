package railo.runtime.interpreter.ref.op;

import java.math.BigDecimal;
import java.math.MathContext;

import railo.commons.math.MathUtil;
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
		return MathUtil.divide(getLeft(pc),getRight(pc)).toString();
	}
    

}
