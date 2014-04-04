package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.functions.closure.Some;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.UDF;

public class ArraySome extends BIF {

	private static final long serialVersionUID = -7449844630816343951L;

	public static boolean call(PageContext pc , Array array, UDF udf) throws PageException {
		return _call(pc, array, udf, false, 20);
	}
	public static boolean call(PageContext pc , Array array, UDF udf, boolean parallel) throws PageException {
		return _call(pc, array, udf, parallel, 20);
	}

	public static boolean call(PageContext pc , Array array, UDF udf, boolean parallel, double maxThreads) throws PageException {
		return _call(pc, array, udf, parallel, (int)maxThreads);
	}
	private static boolean _call(PageContext pc , Array array, UDF udf, boolean parallel, int maxThreads) throws PageException {
		return Some._call(pc, array, udf, parallel, maxThreads);
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)
			return call(pc, Caster.toArray(args[0]), Caster.toFunction(args[1]));
		if(args.length==3)
			return call(pc, Caster.toArray(args[0]), Caster.toFunction(args[1]), Caster.toBooleanValue(args[2]));
		if(args.length==4)
			return call(pc, Caster.toArray(args[0]), Caster.toFunction(args[1]), Caster.toBooleanValue(args[2]), Caster.toDoubleValue(args[3]));
		
		throw new FunctionException(pc,"ArraySome",2,4,args.length);
		
	}

}
