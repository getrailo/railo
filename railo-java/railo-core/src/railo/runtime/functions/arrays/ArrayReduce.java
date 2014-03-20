/**
 * Implements the CFML Function ArrayFilter
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.functions.closure.Reduce;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.UDF;


public final class ArrayReduce extends BIF {
	
	private static final long serialVersionUID = 7832440197492225852L;

	public static Object call(PageContext pc , Array array, UDF udf) throws PageException {
		return Reduce._call(pc, array, udf,null);
	}
	
	public static Object call(PageContext pc , Array array, UDF udf, Object initValue) throws PageException {
		return Reduce._call(pc, array, udf,initValue);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)
			return call(pc, Caster.toArray(args[0]), Caster.toFunction(args[1]));
		if(args.length==3)
			return call(pc, Caster.toArray(args[0]), Caster.toFunction(args[1]), args[2]);
		
		throw new FunctionException(pc, "ArrayReduce", 2, 3, args.length);
	}
}