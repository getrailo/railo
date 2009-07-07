/**
 * Implements the Cold Fusion Function listlen
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class ListLen implements Function {
	public static double call(PageContext pc , String list) {
		return List.len(list,',');
	}
	public static double call(PageContext pc , String list, String delimter) {
		return List.len(list,delimter);
	}
	


	
}