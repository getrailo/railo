/**
 * Implements the CFML Function listrest
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.util.ListUtil;

public final class ListRest implements Function {
	
	private static final long serialVersionUID = -6596215135126751629L;
	
	public static String call(PageContext pc , String list) {
		return ListUtil.rest(list, ",",true);
	}
	public static String call(PageContext pc , String list, String delimiter) {
		return ListUtil.rest(list, delimiter,true);
	}
	public static String call(PageContext pc , String list, String delimiter, boolean includeEmptyFields) {
		return ListUtil.rest(list, delimiter,!includeEmptyFields);
	}
}