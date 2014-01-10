/**
 * Implements the CFML Function arraysort
 */
package railo.runtime.functions.arrays;

import java.util.Comparator;

import railo.runtime.PageContext;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.Closure;
import railo.runtime.type.UDF;
import railo.runtime.type.util.ArrayUtil;

public final class ArraySort extends BIF {

	private static final long serialVersionUID = -747941236369495141L;

	public static boolean call(PageContext pc , Array array, Object sortTypeOrClosure) throws PageException {
		return call(pc , array, sortTypeOrClosure, "asc",false);
	}
	public static boolean call(PageContext pc , Array array, Object sortTypeOrClosure, String sortorder) throws PageException {
		return call(pc , array, sortTypeOrClosure, sortorder,false);
	}
	
	public static boolean call(PageContext pc , Array array, Object sortTypeOrClosure, String sortorder, boolean localeSensitive) throws PageException {
		if(array.getDimension()>1)
			throw new ExpressionException("only 1 dimensional arrays can be sorted");

		if(sortTypeOrClosure instanceof UDF){
			UDFComparator comp=new UDFComparator(pc, (UDF)sortTypeOrClosure);
			array.sort(comp);
		}
		else {
			array.sort(ArrayUtil.toComparator(pc,Caster.toString(sortTypeOrClosure), sortorder,localeSensitive));
		}
		return true;
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)return call(pc,Caster.toArray(args[0]),args[1]);
		if(args.length==3)return call(pc,Caster.toArray(args[0]),args[1],Caster.toString(args[2]));
		return call(pc,Caster.toArray(args[0]),args[1],Caster.toString(args[2]),Caster.toBooleanValue(args[3]));
	}
}

class UDFComparator implements Comparator<Object> {

	private UDF udf;
	private Object[] args=new Object[2];
	private PageContext pc;
	
	public UDFComparator(PageContext pc,UDF udf){
		this.pc=pc;
		this.udf=udf;
	}

	@Override
	public int compare(Object oLeft, Object oRight) {
		try {
			args[0]=oLeft;
			args[1]=oRight;
			Object res = udf.call(pc, args, false);
			Integer i = Caster.toInteger(res,null);
			if(i==null) throw new FunctionException(pc,"ArraySort",2,"function","return value of the "+(udf instanceof Closure?"closure":"function ["+udf.getFunctionName()+"]")+" cannot be casted to a integer.",CasterException.createMessage(res, "integer"));
        	return i.intValue();
		} 
		catch (PageException pe) {
			throw new PageRuntimeException(pe);
		}
	}

}