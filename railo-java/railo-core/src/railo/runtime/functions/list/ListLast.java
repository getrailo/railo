package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.util.ListUtil;

/**
 * Implements the CFML Function listlast
 */
public final class ListLast implements Function {
	
	private static final long serialVersionUID = 2822477678831478329L;
	
	public static String call(PageContext pc , String list) {
		return ListUtil.last(list,",",true);
	}
	public static String call(PageContext pc , String list, String delimiter) {
		return ListUtil.last(list,delimiter,true);
	}
	public static String call(PageContext pc , String list, String delimiter, boolean includeEmptyFields) {
		return ListUtil.last(list,delimiter,!includeEmptyFields);
	}
	
}