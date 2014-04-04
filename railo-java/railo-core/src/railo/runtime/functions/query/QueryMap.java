package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.functions.closure.Map;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;
import railo.runtime.type.UDF;

public class QueryMap extends BIF {

	private static final long serialVersionUID = 5225631181634029456L;

	public static Query call(PageContext pc , Query qry, UDF udf) throws PageException {
		return _call(pc, qry, udf, false, 20);
	}

	public static Query call(PageContext pc , Query qry, UDF udf, boolean parallel) throws PageException {
		return _call(pc, qry, udf, parallel, 20);
	}

	public static Query call(PageContext pc , Query qry, UDF udf, boolean parallel, double maxThreads) throws PageException {
		return _call(pc, qry, udf, parallel, (int)maxThreads);
	}
	
	private static Query _call(PageContext pc , Query qry, UDF udf, boolean parallel, int maxThreads) throws PageException {
		return (Query) Map._call(pc, qry, udf, parallel, maxThreads);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)
			return call(pc, Caster.toQuery(args[0]), Caster.toFunction(args[1]));
		if(args.length==3)
			return call(pc, Caster.toQuery(args[0]), Caster.toFunction(args[1]), Caster.toBooleanValue(args[2]));
		if(args.length==4)
			return call(pc, Caster.toQuery(args[0]), Caster.toFunction(args[1]), Caster.toBooleanValue(args[2]), Caster.toDoubleValue(args[3]));
		
		throw new FunctionException(pc, "QueryMap", 2, 4, args.length);
	}

}
