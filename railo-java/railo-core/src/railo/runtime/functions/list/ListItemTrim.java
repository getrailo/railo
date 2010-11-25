/**
 * Implements the Cold Fusion Function listtoarray
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.List;

public final class ListItemTrim implements Function {
	
	private static final long serialVersionUID = -2254266180423759499L;

	public static String call(PageContext pc , String list) throws PageException {
		return call(pc,list,",",false);
	}
	public static String call(PageContext pc , String list, String delimeter) throws PageException {
		return call(pc,list,delimeter,false);
	}
	
	public static String call(PageContext pc , String list, String delimeter,boolean includeEmptyFields) throws PageException {
		if(list.length()==0) return "";
		Array arr = includeEmptyFields?List.listToArray(list,delimeter):List.listToArrayRemoveEmpty(list,delimeter);
		return List.arrayToList(List.trimItems(arr),delimeter);	
	}
}