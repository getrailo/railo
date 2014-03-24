/**
 * Implements the CFML Function arrayavg
 */
package railo.runtime.functions.list;

import java.util.Iterator;

import railo.commons.lang.CFTypes;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.closure.Filter;
import railo.runtime.functions.closure.Map;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.UDF;
import railo.runtime.type.util.ListUtil;
import railo.runtime.type.util.StringListData;


public final class ListMap implements Function {

	public static String call(PageContext pc , String list, UDF filter) throws PageException {
		return call(pc, list, filter, ",", false, false, 20);
	}

	public static String call(PageContext pc , String list, UDF filter,String delimiter) throws PageException {
		return call(pc, list, filter, delimiter, false, false, 20);
	}

	public static String call(PageContext pc , String list, UDF filter,String delimiter
			, boolean includeEmptyFields) throws PageException {
		return call(pc, list, filter, delimiter, includeEmptyFields, false, 20);
	}
	
	public static String call(PageContext pc , String list, UDF filter,String delimiter
			, boolean includeEmptyFields, boolean parallel) throws PageException {
		return call(pc, list, filter, delimiter, includeEmptyFields, parallel, 20);
	}

	public static String call(PageContext pc , String list, UDF filter,String delimiter
			, boolean includeEmptyFields, boolean parallel, double maxThreads) throws PageException {
		
		return ListUtil.arrayToList(
				(Array)Map.call(pc, new StringListData(list,delimiter,includeEmptyFields), filter, parallel, maxThreads), delimiter);
	}
}