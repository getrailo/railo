/**
 * Implements the Cold Fusion Function arrayMerge
 * Merge 2 arrays
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;


public final class ArrayReverse implements Function {
	public static Array call(PageContext pc , Array array) throws ExpressionException {
		Array rev=new ArrayImpl(array.getDimension());
		int len=array.size();
		for(int i=0;i<len;i++) {
			try {
				rev.setE(len-i,array.getE(i+1));
			} catch (PageException e) {
			}
		}
		return rev;
	}	
}