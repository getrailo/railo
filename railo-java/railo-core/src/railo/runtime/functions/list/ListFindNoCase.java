/**
 * Implements the CFML Function listfindnocase
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.util.ListUtil;

public final class ListFindNoCase implements Function {
	
	private static final long serialVersionUID = 8596474187680730966L;
	
	public static double call(PageContext pc , String list, String value) {
		return ListUtil.listFindNoCaseIgnoreEmpty(list,value,',')+1;
	}
	public static double call(PageContext pc , String list, String value, String delimter) {
		return ListUtil.listFindNoCaseIgnoreEmpty(list,value,delimter)+1;
	}
	public static double call(PageContext pc , String list, String value, String delimter, boolean includeEmptyFields) {
		if(includeEmptyFields)return ListUtil.listFindNoCase(list,value,delimter)+1;
		return ListUtil.listFindNoCaseIgnoreEmpty(list,value,delimter)+1;
	}
}