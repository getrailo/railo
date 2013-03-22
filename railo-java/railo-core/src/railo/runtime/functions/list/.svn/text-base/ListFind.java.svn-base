package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

/**
 * Implements the Cold Fusion Function listfind
 */
public final class ListFind implements Function {
	public static double call(PageContext pc , String list, String value) {
		return List.listFindIgnoreEmpty(list,value,',')+1;
	}
	public static double call(PageContext pc , String list, String value, String delimter) {
		return List.listFindIgnoreEmpty(list,value,delimter)+1;
	}
}