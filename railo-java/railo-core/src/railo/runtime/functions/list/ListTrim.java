/**
 * Implements the Cold Fusion Function listlast
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class ListTrim implements Function {
	public static String call(PageContext pc , String list) {
		return call(pc,list,",");
	}
	public static String call(PageContext pc , String list, String delimeter) {
		return List.trim(list,delimeter);
	}
}