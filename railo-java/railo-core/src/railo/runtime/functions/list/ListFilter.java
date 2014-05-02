/**
 * Implements the CFML Function arrayavg
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.functions.closure.Filter;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.UDF;
import railo.runtime.type.util.ListUtil;
import railo.runtime.type.util.StringListData;


public final class ListFilter extends BIF {

	private static final long serialVersionUID = 2182867537570796564L;

	public static String call(PageContext pc , String list, UDF filter) throws PageException {
		return call(pc, list, filter, ",", false,true, false, 20);
	}

	public static String call(PageContext pc , String list, UDF filter,String delimiter) throws PageException {
		return call(pc, list, filter, delimiter, false,true, false, 20);
	}

	public static String call(PageContext pc , String list, UDF filter,String delimiter
			, boolean includeEmptyFields) throws PageException {
		return call(pc, list, filter, delimiter, includeEmptyFields,true, false, 20);
	}

	public static String call(PageContext pc , String list, UDF filter,String delimiter
			, boolean includeEmptyFields, boolean multiCharacterDelimiter) throws PageException {
		return call(pc, list, filter, delimiter, includeEmptyFields,multiCharacterDelimiter, false, 20);
	}
	
	public static String call(PageContext pc , String list, UDF filter,String delimiter
			, boolean includeEmptyFields, boolean multiCharacterDelimiter, boolean parallel) throws PageException {
		return call(pc, list, filter, delimiter, includeEmptyFields,multiCharacterDelimiter, parallel, 20);
	}

	public static String call(PageContext pc , String list, UDF filter,String delimiter
			, boolean includeEmptyFields, boolean multiCharacterDelimiter, boolean parallel, double maxThreads) throws PageException {
		
		return ListUtil.arrayToList(
				(Array)Filter.call(pc, new StringListData(list,delimiter,includeEmptyFields,multiCharacterDelimiter), filter, parallel, maxThreads), delimiter);
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
			return call(pc, Caster.toString(args[0]), Caster.toFunction(args[1]),Caster.toString(args[2]),Caster.toBooleanValue(args[3]), Caster.toBooleanValue(args[4]), Caster.toBooleanValue(args[5]));
		if(args.length==7)
			return call(pc, Caster.toString(args[0]), Caster.toFunction(args[1]),Caster.toString(args[2]),Caster.toBooleanValue(args[3]), Caster.toBooleanValue(args[4]),Caster.toBooleanValue(args[5]), Caster.toDoubleValue(args[6]));
		
		throw new FunctionException(pc, "ListFilter", 2, 7, args.length);
	}
}