/**
 * Implements the Cold Fusion Function listtoarray
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.List;

public final class ListToArray implements Function {
	public static Array call(PageContext pc , String list) {
		if(list.length()==0) 
			return new ArrayImpl();
		return List.listToArrayRemoveEmpty(list,',');
	}
	public static Array call(PageContext pc , String list, String delimeter) {
		return call(pc, list,delimeter,false);
	}
	

	public static Array call(PageContext pc , String list, String delimeter,boolean includeEmptyFields) {
		if(list.length()==0) 
			return new ArrayImpl();
		if(includeEmptyFields)return List.listToArray(list,delimeter);
		return List.listToArrayRemoveEmpty(list,delimeter);
	}
	
	
	
}