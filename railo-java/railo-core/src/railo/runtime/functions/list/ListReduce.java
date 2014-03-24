package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.functions.closure.Reduce;
import railo.runtime.op.Caster;
import railo.runtime.type.UDF;


public final class ListReduce extends BIF {

	private static final long serialVersionUID = 1857478124366819325L;

	public static Object call(PageContext pc , String list, UDF udf) throws PageException {
		return Reduce._call(pc, list, udf,null);
	}
	
	public static Object call(PageContext pc , String list, UDF udf, Object initValue) throws PageException {
		return call(pc, list, udf, initValue, ",", false);
	}
	
	public static Object call(PageContext pc , String list, UDF udf, Object initValue ,String delimiter) throws PageException {
		return call(pc, list, udf, initValue, delimiter, false);
	}
	
	public static Object call(PageContext pc , String list, UDF udf, Object initValue ,String delimiter, boolean includeEmptyFields) throws PageException {
		return Reduce._call(pc, list, udf,initValue);
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {

		if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toFunction(args[1]));
		if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toFunction(args[1]),args[2]);
		if(args.length==4)
			return call(pc, Caster.toString(args[0]), Caster.toFunction(args[1]),args[2],Caster.toString(args[3]));
		if(args.length==5)
			return call(pc, Caster.toString(args[0]), Caster.toFunction(args[1]),args[2],Caster.toString(args[3]),Caster.toBooleanValue(args[4]));
		
		throw new FunctionException(pc, "ListReduce", 2, 5, args.length);
	}
}