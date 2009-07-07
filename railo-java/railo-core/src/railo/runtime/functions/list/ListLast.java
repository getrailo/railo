package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

/**
 * Implements the Cold Fusion Function listlast
 */
public final class ListLast implements Function {
	public static String call(PageContext pc , String list) {
		return List.last(list,",");
	}
	public static String call(PageContext pc , String list, String delimeter) {
		return List.last(list,delimeter);
	}
	
}