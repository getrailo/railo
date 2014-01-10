/**
 * Implements the CFML Function listtoarray
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.util.ListUtil;

public final class ListItemTrim implements Function {
	
	private static final long serialVersionUID = -2254266180423759499L;

	public static String call(PageContext pc , String list) throws PageException {
		return call(pc,list,",",false);
	}
	public static String call(PageContext pc , String list, String delimiter) throws PageException {
		return call(pc,list,delimiter,false);
	}
	
	public static String call(PageContext pc , String list, String delimiter,boolean includeEmptyFields) throws PageException {
		if(list.length()==0) return "";
		Array arr = includeEmptyFields?ListUtil.listToArray(list,delimiter):ListUtil.listToArrayRemoveEmpty(list,delimiter);
		return ListUtil.arrayToList(ListUtil.trimItems(arr),delimiter);	
	}
}