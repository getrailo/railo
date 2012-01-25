/**
 * Implements the Cold Fusion Function arrayavg
 */
package railo.runtime.functions.list;

import railo.print;
import railo.commons.lang.CFTypes;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.List;
import railo.runtime.type.UDF;


public final class ListFilter implements Function {
	

	public static String call(PageContext pc , String list, UDF filter) throws PageException {
		return call(pc, list, filter,",");
	}

	public static String call(PageContext pc , String list, UDF filter, String delimeter) throws PageException {
		// check UDF return type
		int type = filter.getReturnType();
		if(type!=CFTypes.TYPE_BOOLEAN && type!=CFTypes.TYPE_ANY)
			throw new ExpressionException("invalid return type ["+filter.getReturnTypeAsString()+"] for UDF Filter, valid return types are [boolean,any]");
		
		// check UDF arguments
		FunctionArgument[] args = filter.getFunctionArguments();
		if(args.length>1)
			throw new ExpressionException("UDF filter has to many arguments ["+args.length+"], should have at maximum 1 argument");
		
		if(delimeter==null) delimeter=",";
		Array array = List.listToArrayRemoveEmpty(list, delimeter);
		
		
		StringBuilder sb=new StringBuilder();
		Key[] keys = array.keys();
		Object value;
		for(int i=0;i<keys.length;i++){
			value=array.get(keys[i]);
			if(Caster.toBooleanValue(filter.call(pc, new Object[]{value}, true))){
				if(sb.length()>0) sb.append(delimeter);
				sb.append(value);
			}
		}
		return sb.toString();
	}
}