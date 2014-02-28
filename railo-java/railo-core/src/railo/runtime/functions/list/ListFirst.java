/**
 * Implements the CFML Function listfirst
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.util.ListUtil;

public final class ListFirst implements Function {
	
	private static final long serialVersionUID = 1098339742182832847L;
	
	public static String call(PageContext pc, String list) {
		return ListUtil.first(list, ",", true, 1);
	}
	
	public static String call(PageContext pc, String list, String delimiter) {
		return ListUtil.first(list, delimiter, true, 1);
	}
	
	public static String call(PageContext pc, String list, String delimiter, boolean includeEmptyFields) {
		return ListUtil.first(list, delimiter, !includeEmptyFields, 1);
	}

	public static String call(PageContext pc, String list, String delimiter, boolean includeEmptyFields, double count) {

		return ListUtil.first(list, delimiter, !includeEmptyFields, (int)count);
	}
}