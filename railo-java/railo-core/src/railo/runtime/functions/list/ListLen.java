/**
 * Implements the CFML Function listlen
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.util.ListUtil;

public final class ListLen implements Function {
	
	private static final long serialVersionUID = -592317399255505765L;
	
	public static double call(PageContext pc , String list) {
		return ListUtil.len(list,',',true);
	}
	public static double call(PageContext pc , String list, String delimter) {
		return ListUtil.len(list,delimter,true);
	}
	public static double call(PageContext pc , String list, String delimter, boolean includeEmptyFields) {
		return ListUtil.len(list,delimter,!includeEmptyFields);
	}
	


	
}