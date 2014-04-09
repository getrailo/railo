/**
 * Implements the CFML Function arrayavg
 */
package railo.runtime.functions.struct;

import railo.commons.lang.CFTypes;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.functions.closure.Filter;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
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
		
		return (Struct) Filter._call(pc, sct, filter, parallel, maxThreads);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		
		if(args.length==2)
			return call(pc, Caster.toStruct(args[0]), Caster.toFunction(args[1]));
		if(args.length==3)
			return call(pc, Caster.toStruct(args[0]), Caster.toFunction(args[1]), Caster.toBooleanValue(args[2]));
		if(args.length==4)
			return call(pc, Caster.toStruct(args[0]), Caster.toFunction(args[1]), Caster.toBooleanValue(args[2]), Caster.toDoubleValue(args[3]));
		
		throw new FunctionException(pc, "StructFilter", 2, 4, args.length);
	}
}