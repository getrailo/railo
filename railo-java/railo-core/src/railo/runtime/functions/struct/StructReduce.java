/**
 * Implements the CFML Function ArrayFilter
 */
package railo.runtime.functions.struct;
import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.functions.closure.Reduce;
import railo.runtime.op.Caster;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;


public final class StructReduce extends BIF {
	
	private static final long serialVersionUID = 2268062574022295144L;

	public static Object call(PageContext pc , Struct sct, UDF udf) throws PageException {
		return Reduce._call(pc, sct, udf,null);
	}
	
	public static Object call(PageContext pc , Struct sct, UDF udf, Object initValue) throws PageException {
		return Reduce._call(pc, sct, udf,initValue);
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)
			return call(pc, Caster.toStruct(args[0]), Caster.toFunction(args[1]));
		if(args.length==3)
			return call(pc, Caster.toStruct(args[0]), Caster.toFunction(args[1]), args[2]);
		
		throw new FunctionException(pc, "StructReduce", 2, 3, args.length);
	}
}