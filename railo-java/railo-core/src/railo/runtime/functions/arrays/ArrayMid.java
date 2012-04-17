package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;

public final class ArrayMid implements Function {

	private static final long serialVersionUID = 4996354700884413289L;

	public static Array call(PageContext pc , Array arr, double start) throws ExpressionException {
		return call(pc,arr,start,-1);
	}
	
	public static Array call(PageContext pc , Array arr, double start, double count) throws ExpressionException {
		int s=(int) start;
		int c=(int) count;
		
		if(s<1) throw new FunctionException(pc, "ArrayMid", 2, "start", "Parameter which is now ["+s+"] must be a positive integer");
		if(c==-1) c=arr.size();
		else if(c<-1) throw new FunctionException(pc, "ArrayMid", 3, "count", "Parameter which is now ["+c+"] must be a non-negative integer or -1 (for string length)");
		c+=s-1;
		if(s>arr.size()) return new ArrayImpl();
		
		ArrayImpl rtn = new ArrayImpl();
		int len = arr.size();
		Object value;
		for(int i=s;i<=c && i<=len ;i++){
			value=arr.get(i, null);
			rtn.appendEL(value);
		}
		return rtn;
	}
}