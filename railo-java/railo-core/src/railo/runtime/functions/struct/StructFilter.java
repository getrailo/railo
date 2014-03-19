/**
 * Implements the CFML Function arrayavg
 */
package railo.runtime.functions.struct;

import java.util.Iterator;
import java.util.Map.Entry;

import railo.commons.lang.CFTypes;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.functions.closure.Filter;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Array;
import railo.runtime.type.FunctionArgument;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;


public final class StructFilter extends BIF {

	public static Struct call(PageContext pc , Struct sct, UDF udf) throws PageException {
		return _call(pc, sct, udf, false, 20);
	}
	public static Struct call(PageContext pc , Struct sct, UDF udf, boolean parallel) throws PageException {
		return _call(pc, sct, udf, parallel, 20);
	}

	public static Struct call(PageContext pc , Struct sct, UDF udf, boolean parallel, double maxThreads) throws PageException {
		return _call(pc, sct, udf, parallel, (int)maxThreads);
	}
	
	
	public static Struct _call(PageContext pc , Struct sct, UDF filter, boolean parallel, int maxThreads) throws PageException {	

		// check UDF return type
		int type = filter.getReturnType();
		if(type!=CFTypes.TYPE_BOOLEAN && type!=CFTypes.TYPE_ANY)
			throw new ExpressionException("invalid return type ["+filter.getReturnTypeAsString()+"] for UDF Filter, valid return types are [boolean,any]");
		
		// check UDF arguments
		//FunctionArgument[] args = filter.getFunctionArguments();
		//if(args.length>2)
		//	throw new ExpressionException("UDF filter has to many arguments ["+args.length+"], should have at maximum 2 arguments");
		
		return (Struct) Filter._call(pc, sct, filter, parallel, maxThreads);
		
		/*Struct rtn=new StructImpl();
		//Key[] keys = sct.keys();
		Iterator<Entry<Key, Object>> it = sct.entryIterator();
		Object value;
		while(it.hasNext()){
			Entry<Key, Object> e = it.next();
			value=e.getValue();
			if(Caster.toBooleanValue(filter.call(pc, new Object[]{e.getKey().getString(),value}, true)))
				rtn.set(e.getKey(), value);
		}
		return rtn;*/
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toStruct(args[0]),Caster.toFunction(args[1]));
	}
}