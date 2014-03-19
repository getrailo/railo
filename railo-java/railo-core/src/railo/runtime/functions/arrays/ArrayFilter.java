/**
 * Implements the CFML Function ArrayFilter
 */
package railo.runtime.functions.arrays;

import java.util.Iterator;

import railo.commons.lang.CFTypes;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.functions.closure.Filter;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.UDF;


public final class ArrayFilter extends BIF {
	
	public static Array call(PageContext pc , Array array, UDF udf) throws PageException {
		return _call(pc, array, udf, false, 20);
	}
	public static Array call(PageContext pc , Array array, UDF udf, boolean parallel) throws PageException {
		return _call(pc, array, udf, parallel, 20);
	}

	public static Array call(PageContext pc , Array array, UDF udf, boolean parallel, double maxThreads) throws PageException {
		return _call(pc, array, udf, parallel, (int)maxThreads);
	}

	public static Array _call(PageContext pc , Array array, UDF filter, boolean parallel, int maxThreads) throws PageException {
		// check UDF return type
		int type = filter.getReturnType();
		if(type!=CFTypes.TYPE_BOOLEAN && type!=CFTypes.TYPE_ANY)
			throw new ExpressionException("invalid return type ["+filter.getReturnTypeAsString()+"] for UDF Filter; valid return types are [boolean,any]");
		
		// check UDF arguments
		//FunctionArgument[] args = filter.getFunctionArguments();
		//if(args.length>1)
		//	throw new ExpressionException("UDF filter has too many arguments ["+args.length+"], should have at maximum 1 argument");
		
		
		return (Array) Filter._call(pc, array, filter, parallel, maxThreads);
		
		/*Array rtn=new ArrayImpl();
		Iterator<Object> it = array.valueIterator();
		Object value;
		while(it.hasNext()){
			value=it.next();
			if(Caster.toBooleanValue(filter.call(pc, new Object[]{value}, true)))
				rtn.append(value);
		}
		return rtn;*/
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toArray(args[0]),Caster.toFunction(args[1]));
	}
}