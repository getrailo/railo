/**
 * Implements the CFML Function listlast
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.util.ListUtil;

public final class ListCompact implements Function {
	public static String call(PageContext pc , String list) {
		return call(pc,list,",");
	}
	public static String call(PageContext pc , String list, String delimiter) {
		return ListUtil.trim(list,delimiter);
	}
}