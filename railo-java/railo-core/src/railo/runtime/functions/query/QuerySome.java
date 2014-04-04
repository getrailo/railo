package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.functions.closure.Some;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;
import railo.runtime.type.UDF;

public class QuerySome extends BIF {

	private static final long serialVersionUID = 8316121450166554384L;

	public static boolean call(PageContext pc , Query qry, UDF udf) throws PageException {
		return _call(pc, qry, udf, false, 20);
	}
	public static boolean call(PageContext pc , Query qry, UDF udf, boolean parallel) throws PageException {
		return _call(pc, qry, udf, parallel, 20);
	}

	public static boolean call(PageContext pc , Query qry, UDF udf, boolean parallel, double maxThreads) throws PageException {
		return _call(pc, qry, udf, parallel, (int)maxThreads);
	}
	private static boolean _call(PageContext pc , Query qry, UDF udf, boolean parallel, int maxThreads) throws PageException {
		return Some._call(pc, qry, udf, parallel, maxThreads);
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)
			return call(pc, Caster.toQuery(args[0]), Caster.toFunction(args[1]));
		if(args.length==3)
			return call(pc, Caster.toQuery(args[0]), Caster.toFunction(args[1]), Caster.toBooleanValue(args[2]));
		if(args.length==4)
			return call(pc, Caster.toQuery(args[0]), Caster.toFunction(args[1]), Caster.toBooleanValue(args[2]), Caster.toDoubleValue(args[3]));
		
		throw new FunctionException(pc,"QuerySome",2,4,args.length);
		
	}

}
