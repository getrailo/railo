/**
 * Implements the CFML Function listcontainsnocase
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.util.ListUtil;

public final class ListContainsNoCase implements Function {
	
	private static final long serialVersionUID = 4955787566835292639L;
	
	public static double call(PageContext pc , String list, String value) {
		return call(pc, list, value, ",", false);
	}
	public static double call(PageContext pc , String list, String value, String delimter) {
		return call(pc, list, value, delimter, false);
	}
	public static double call(PageContext pc , String list, String value, String delimter, boolean includeEmptyFields) {
		if(includeEmptyFields)return ListUtil.listContainsNoCase(list,value,delimter)+1;
		return ListUtil.listContainsIgnoreEmptyNoCase(list,value,delimter)+1;
	}
}