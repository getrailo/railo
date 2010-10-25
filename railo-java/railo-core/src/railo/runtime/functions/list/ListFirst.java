/**
 * Implements the Cold Fusion Function listfirst
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class ListFirst implements Function {
	public static String call(PageContext pc , String list) {
		return List.first(list,",");
	}
	public static String call(PageContext pc , String list, String delimeter) {
		return List.first(list,delimeter);
	}
}