/**
 * Implements the Cold Fusion Function arraymin
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;

public final class ArraySlice implements Function {
	
	private static final long serialVersionUID = 7309769117464009924L;

	public static Array call(PageContext pc , Array arr,double offset) throws PageException {
		return call(pc , arr, offset,0);
	}
	public static Array call(PageContext pc , Array arr,double offset,double length) throws PageException {
				
		int len=arr.size();
		if(offset>0) {
			if(len<offset)throw new FunctionException(pc,"arraySlice",2,"offset","offset cannot be greater than size of the array");
			
			int to=0;
			if(length>0)to=(int)(offset+length-1);
			else if(length<0)to=(int)(len+length);
			if(len<to)
				throw new FunctionException(pc,"arraySlice",3,"length","offset+length cannot be greater than size of the array");
			
			return get(arr,(int)offset,to);
		}
		return call(pc ,arr,len+offset,length);
	}
	
	private static Array get(Array arr, int from, int to) throws PageException {
		Array rtn=new ArrayImpl(arr.getDimension());
		int[] keys=arr.intKeys();
		for(int i=0;i<keys.length;i++) {
			int key=keys[i];
			if(key<from)continue;
			if(to>0 && key>to)break;
			rtn.append(arr.getE(key));
		}
		return rtn;
	}
	
}