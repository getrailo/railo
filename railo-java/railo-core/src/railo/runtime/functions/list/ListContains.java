/**
 * Implements the CFML Function listcontains
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.util.ListUtil;

public final class ListContains implements Function {

	private static final long serialVersionUID = -7580788340022587225L;

	public static double call(PageContext pc , String list, String value) {
		return call(pc, list, value, ",", false);
	}
	public static double call(PageContext pc , String list, String value, String delimter) {
		return call(pc, list, value, delimter, false);
	}
	public static double call(PageContext pc , String list, String value, String delimter, boolean includeEmptyFields) {
		if(includeEmptyFields)return ListUtil.listContains(list,value,delimter)+1;
		return ListUtil.listContainsIgnoreEmpty(list,value,delimter)+1;
	}
}