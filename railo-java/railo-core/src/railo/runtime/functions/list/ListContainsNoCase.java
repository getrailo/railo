/**
 * Implements the Cold Fusion Function listcontainsnocase
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class ListContainsNoCase implements Function {
	public static double call(PageContext pc , String list, String value) {
		return List.listContainsIgnoreEmptyNoCase(list,value,",")+1;
	}
	public static double call(PageContext pc , String list, String value, String delimter) {
		return List.listContainsIgnoreEmptyNoCase(list,value,delimter)+1;
	}
}