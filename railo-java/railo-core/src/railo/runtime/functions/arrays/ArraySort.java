/**
 * Implements the CFML Function arraysort
 */
package railo.runtime.functions.arrays;

import java.util.Comparator;

import railo.runtime.PageContext;
import railo.runtime.exp.CasterException;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.exp.PageRuntimeException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Closure;
import railo.runtime.type.UDF;

public final class ArraySort implements Function {
	public static boolean call(PageContext pc , Array array, Object sortTypeOrClosure) throws PageException {
		return call(pc , array, sortTypeOrClosure, "asc");
	}
	public static boolean call(PageContext pc , Array array, Object sortTypeOrClosure, String sortorder) throws PageException {
		if(sortTypeOrClosure instanceof UDF){
			UDFComparator comp=new UDFComparator(pc, (UDF)sortTypeOrClosure);
			array.sort(comp);
		}
		else array.sort(Caster.toString(sortTypeOrClosure), sortorder);
		return true;
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