/**
 * Implements the ColdFusion Function arrayAvg
 */
package railo.runtime.functions.arrays;

import java.util.Iterator;

import railo.commons.lang.CFTypes;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.UDF;


public final class ArrayFilter implements Function {

	private static final long serialVersionUID = 7710446268528845873L;

	public static Array call(PageContext pc , Array array, UDF filter) throws PageException {
		// check UDF return type
		int type = filter.getReturnType();
		if(type!=CFTypes.TYPE_BOOLEAN && type!=CFTypes.TYPE_ANY)
			throw new ExpressionException("Invalid return type ["+filter.getReturnTypeAsString()+"] for UDF Filter; valid return types are [boolean,any]");
		
		// check UDF arguments
		FunctionArgument[] args = filter.getFunctionArguments();
		if(args.length>1)
			throw new ExpressionException("UDF filter has too many arguments ["+args.length+"]; should have at maximum 1 argument");
		
		
		Array rtn=new ArrayImpl();
		Iterator<Object> it = array.valueIterator();
		Object value;
		while(it.hasNext()){
			value=it.next();
			if(Caster.toBooleanValue(filter.call(pc, new Object[]{value}, true)))
				rtn.append(value);
		}
		return rtn;
	}
}