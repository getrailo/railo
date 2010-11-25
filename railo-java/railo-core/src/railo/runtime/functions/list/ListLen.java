/**
 * Implements the Cold Fusion Function listlen
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class ListLen implements Function {
	
	private static final long serialVersionUID = -592317399255505765L;
	
	public static double call(PageContext pc , String list) {
		return List.len(list,',',true);
	}
	public static double call(PageContext pc , String list, String delimter) {
		return List.len(list,delimter,true);
	}
	public static double call(PageContext pc , String list, String delimter, boolean includeEmptyFields) {
		return List.len(list,delimter,!includeEmptyFields);
	}
	


	
}