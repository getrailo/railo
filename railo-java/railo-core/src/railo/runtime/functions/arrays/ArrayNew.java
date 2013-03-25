/**
 * Implements the CFML Function arraynew
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;

public final class ArrayNew extends BIF {

	private static final long serialVersionUID = -5923269433550568279L;

	public static Array call(PageContext pc) throws ExpressionException  {
		return new ArrayImpl(1);
	}
	
	public static Array call(PageContext pc , double number) throws ExpressionException {
		return new ArrayImpl((int)number);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==0) return call(pc);
		return call(pc,Caster.toDoubleValue(args[0]));
	}
	
}