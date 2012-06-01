/**
 * Implements the CFML Function arrayMerge
 * Merge 2 arrays
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;


public final class ArrayMerge implements Function {
	public static Array call(PageContext pc , Array arr1, Array arr2) throws PageException {
		return call(pc,arr1,arr2,false);
	}
	public static Array call(PageContext pc , Array arr1, Array arr2, boolean leaveIndex) throws PageException {
		if(leaveIndex) {
			Array arr = new ArrayImpl();
			set(arr,arr2);
			set(arr,arr1);
			return arr;			
		}
		
			Array arr = new ArrayImpl();
			append(arr,arr1);
			append(arr,arr2);
			return arr;
		
	}

	public static void set(Array target,Array source) throws PageException {
		int[] srcKeys=source.intKeys();
		for(int i=0;i<srcKeys.length;i++) {
			target.setE(srcKeys[i],source.getE(srcKeys[i]));
		}
	}
	
	public static void append(Array target,Array source) throws PageException {
		int[] srcKeys=source.intKeys();
		for(int i=0;i<srcKeys.length;i++) {
			target.append(source.getE(srcKeys[i]));
		}
	}
	
}