package railo.runtime.interpreter.ref.op;

import java.math.BigDecimal;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.interpreter.ref.Ref;
import railo.runtime.interpreter.ref.RefSupport;
import railo.runtime.interpreter.ref.literal.LBigDecimal;
import railo.runtime.op.Caster;

/**
 * Plus operation
 */
public abstract class Big extends RefSupport implements Ref {

	private Ref right;
	private Ref left;

	/**
	 * constructor of the class
	 * @param left
	 * @param right
	 */
	public Big(Ref left, Ref right) {
		
		
		this.left=left;
		this.right=right;
	}

	protected static BigDecimal toBigDecimal(PageContext pc,Ref ref) throws PageException {
		if(ref instanceof LBigDecimal) return ((LBigDecimal)ref).getBigDecimal();
		return new BigDecimal(Caster.toString(ref.getValue(pc)));
	}

	protected final BigDecimal getLeft(PageContext pc) throws PageException {
		return toBigDecimal(pc,left);
	}
	
	protected final BigDecimal getRight(PageContext pc) throws PageException {
		return toBigDecimal(pc,right);
	}

	@Override
    public final String getTypeName() {
		return "operation";
	}

}
