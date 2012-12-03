/**
 * Implements the CFML Function listlast
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.tag.util.DeprecatedUtil;

public final class ListTrim implements Function {
	
	private static final long serialVersionUID = 2354456835027080741L;
	
	public static String call(PageContext pc , String list) {
		DeprecatedUtil.function(pc,"ListTrim","ListCompact");
		return ListCompact.call(pc,list,",");
	}
	public static String call(PageContext pc , String list, String delimiter) {
		DeprecatedUtil.function(pc,"ListTrim","ListCompact");
		return ListCompact.call(pc,list,delimiter);
	}
}