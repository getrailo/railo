/**
 * Implements the Cold Fusion Function listrest
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class ListRest implements Function {
	public static String call(PageContext pc , String string) {
		return call(pc,string,",");
	}
	public static String call(PageContext pc , String list, String delimeter) {
		return List.rest(list, delimeter);
	}
}