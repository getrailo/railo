package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.functions.closure.Map;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;

public class StructMap extends BIF {

	private static final long serialVersionUID = 5225631181634029456L;

	public static Struct call(PageContext pc , Struct sct, UDF udf) throws PageException {
		return _call(pc, sct, udf, false, 20);
	}
	public static Struct call(PageContext pc , Struct sct, UDF udf, boolean parallel) throws PageException {
		return _call(pc, sct, udf, parallel, 20);
	}

	public static Struct call(PageContext pc , Struct sct, UDF udf, boolean parallel, double maxThreads) throws PageException {
		return _call(pc, sct, udf, parallel, (int)maxThreads);
	}
	private static Struct _call(PageContext pc , Struct sct, UDF udf, boolean parallel, int maxThreads) throws PageException {
		return (Struct) Map._call(pc, sct, udf, parallel, maxThreads);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)
			return call(pc, Caster.toStruct(args[0]), Caster.toFunction(args[1]));
		if(args.length==3)
			return call(pc, Caster.toStruct(args[0]), Caster.toFunction(args[1]), Caster.toBooleanValue(args[2]));
		if(args.length==4)
			return call(pc, Caster.toStruct(args[0]), Caster.toFunction(args[1]), Caster.toBooleanValue(args[2]), Caster.toDoubleValue(args[3]));
		
		throw new FunctionException(pc, "StructMap", 2, 4, args.length);
	}

}
