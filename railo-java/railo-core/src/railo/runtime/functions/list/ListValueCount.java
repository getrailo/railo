/**
 * Implements the Cold Fusion Function listvaluecount
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.List;

public final class ListValueCount implements Function {
	public static double call(PageContext pc , String list, String value) throws PageException {
		return call(pc,list,value,",");
	}
	public static double call(PageContext pc , String list, String value, String delimeter) throws PageException {
		
		
		int count=0;
		//String[] arr= List.listToArray(list,delimeter);
		Array arr= List.listToArray(list,delimeter);
		int len=arr.size();
		
		for(int i=1;i<=len;i++) {
			if(arr.getE(i).equals(value))count++;
		}
		return count;
	}
}