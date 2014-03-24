package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.functions.closure.Reduce;
import railo.runtime.op.Caster;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;


public final class QueryReduce extends BIF {

	private static final long serialVersionUID = 5904086243389763661L;

	public static Object call(PageContext pc , Query qry, UDF udf) throws PageException {
		return Reduce._call(pc, qry, udf,null);
	}
	
	public static Object call(PageContext pc , Query qry, UDF udf, Object initValue) throws PageException {
		return Reduce._call(pc, qry, udf,initValue);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)
			return call(pc, Caster.toQuery(args[0]), Caster.toFunction(args[1]));
		if(args.length==3)
			return call(pc, Caster.toQuery(args[0]), Caster.toFunction(args[1]), args[2]);
		
		throw new FunctionException(pc, "QueryReduce", 2, 3, args.length);
	}
}