/**
 * Implements the CFML Function arrayavg
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.functions.closure.Every;
import railo.runtime.op.Caster;
import railo.runtime.type.UDF;
import railo.runtime.type.util.StringListData;


public final class ListEvery extends BIF {

	public static boolean call(PageContext pc , String list, UDF udf) throws PageException {
		return _call(pc, list, udf,",",false, false, 20);
	}

	public static boolean call(PageContext pc , String list, UDF udf,String delimiter) throws PageException {
		return _call(pc, list, udf,delimiter,false, false, 20);
	}

	public static boolean call(PageContext pc , String list, UDF udf,String delimiter, boolean includeEmptyFields) throws PageException {
		return _call(pc, list, udf,delimiter,includeEmptyFields, false, 20);
	}
	
	public static boolean call(PageContext pc , String list, UDF udf,String delimiter, boolean includeEmptyFields, boolean parallel) throws PageException {
		return _call(pc, list, udf,delimiter,includeEmptyFields, parallel, 20);
	}

	public static boolean call(PageContext pc , String list, UDF udf,String delimiter, boolean includeEmptyFields, boolean parallel, double maxThreads) throws PageException {
		return _call(pc, list, udf,delimiter,includeEmptyFields, parallel, (int)maxThreads);
	}
	private static boolean _call(PageContext pc , String list, UDF udf,String delimiter, boolean includeEmptyFields, boolean parallel, int maxThreads) throws PageException {
		StringListData data=new StringListData(list,delimiter,includeEmptyFields);
		
		return Every.call(pc, data, udf, parallel, maxThreads);
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {

		if(args.length==2)
			return call(pc, Caster.toString(args[0]), Caster.toFunction(args[1]));
		if(args.length==3)
			return call(pc, Caster.toString(args[0]), Caster.toFunction(args[1]),Caster.toString(args[2]));
		if(args.length==4)
			return call(pc, Caster.toString(args[0]), Caster.toFunction(args[1]),Caster.toString(args[2]),Caster.toBooleanValue(args[3]));
		if(args.length==5)
			return call(pc, Caster.toString(args[0]), Caster.toFunction(args[1]),Caster.toString(args[2]),Caster.toBooleanValue(args[3]), Caster.toBooleanValue(args[4]));
		if(args.length==6)
			return call(pc, Caster.toString(args[0]), Caster.toFunction(args[1]),Caster.toString(args[2]),Caster.toBooleanValue(args[3]), Caster.toBooleanValue(args[4]), Caster.toDoubleValue(args[5]));
		
		throw new FunctionException(pc, "ListEvery", 2, 6, args.length);
	}
}