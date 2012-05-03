/**
 * Implements the Cold Fusion Function listfirst
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class ListFirst implements Function {
	
	private static final long serialVersionUID = 1098339742182832847L;
	
	public static String call(PageContext pc , String list) {
		return List.first(list,",",true);
	}
	
	public static String call(PageContext pc , String list, String delimiter) {
		return List.first(list,delimiter,true);
	}
	
	public static String call(PageContext pc , String list, String delimiter, boolean includeEmptyFields) {
		return List.first(list,delimiter,!includeEmptyFields);
	}
}