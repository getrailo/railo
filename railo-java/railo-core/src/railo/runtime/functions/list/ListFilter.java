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
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.UDF;
import railo.runtime.type.util.ListUtil;


public final class ListFilter implements Function {
	

	public static String call(PageContext pc , String list, UDF filter) throws PageException {
		return call(pc, list, filter,",");
	}

	public static String call(PageContext pc , String list, UDF filter, String delimiter) throws PageException {
		// check UDF return type
		int type = filter.getReturnType();
		if(type!=CFTypes.TYPE_BOOLEAN && type!=CFTypes.TYPE_ANY)
			throw new ExpressionException("invalid return type ["+filter.getReturnTypeAsString()+"] for UDF Filter, valid return types are [boolean,any]");
		
		// check UDF arguments
		FunctionArgument[] args = filter.getFunctionArguments();
		if(args.length>1)
			throw new ExpressionException("UDF filter has to many arguments ["+args.length+"], should have at maximum 1 argument");
		
		if(delimiter==null) delimiter=",";
		Array array = ListUtil.listToArrayRemoveEmpty(list, delimiter);
		
		
		StringBuilder sb=new StringBuilder();
		Iterator<Object> it = array.valueIterator();
		Object value;
		while(it.hasNext()){
			value=it.next();
			if(Caster.toBooleanValue(filter.call(pc, new Object[]{value}, true))){
				if(sb.length()>0) sb.append(delimiter);
				sb.append(value);
			}
		}
		return sb.toString();
	}
}