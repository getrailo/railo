/**
 * Implements the Cold Fusion Function listtoarray
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class ListItemTrim implements Function {
	
	public static String call(PageContext pc , String list) throws PageException {
		return call(pc,list,",");
	}
	public static String call(PageContext pc , String list, String delimeter) throws PageException {
		if(list.length()==0) return "";
		return List.arrayToList(List.trimItems(List.listToArray(list,delimeter)),delimeter);	
	}
	
}