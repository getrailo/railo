package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

/**
 * Implements the CFML Function listfind
 */
public final class ListFind implements Function {
	
	private static final long serialVersionUID = -8999117503012321225L;
	
	public static double call(PageContext pc , String list, String value) {
		return List.listFindIgnoreEmpty(list,value,',')+1;
	}
	public static double call(PageContext pc , String list, String value, String delimter) {
		return List.listFindIgnoreEmpty(list,value,delimter)+1;
	}
	public static double call(PageContext pc , String list, String value, String delimter, boolean includeEmptyFields) {
		if(includeEmptyFields)return List.listFind(list,value,delimter)+1;
		return List.listFindIgnoreEmpty(list,value,delimter)+1;
	}
}