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

	private static final long serialVersionUID = -1808030347105091742L;

	public static double call(PageContext pc , String list, String value) throws PageException {
		return call(pc,list,value,",",false);
	}

	public static double call(PageContext pc , String list, String value, String delimeter) throws PageException {
		return call(pc,list,value,delimeter,false);
	}
	public static double call(PageContext pc , String list, String value, String delimeter,boolean includeEmptyFields) throws PageException {
		int count=0;
		Array arr= includeEmptyFields?List.listToArray(list,delimeter):List.listToArrayRemoveEmpty(list,delimeter);
		int len=arr.size();
		
		for(int i=1;i<=len;i++) {
			if(arr.getE(i).equals(value))count++;
		}
		return count;
	}
}