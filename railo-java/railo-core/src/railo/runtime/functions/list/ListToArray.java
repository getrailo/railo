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
	
	private static final long serialVersionUID = 5883854318455975404L;

	public static Array call(PageContext pc , String list) {
		if(list.length()==0) 
			return new ArrayImpl();
		return List.listToArrayRemoveEmpty(list,',');
	}
	public static Array call(PageContext pc , String list, String delimeter) {
		return call(pc, list,delimeter,false,false);
	}
	public static Array call(PageContext pc , String list, String delimeter,boolean includeEmptyFields) {
		return call(pc, list,delimeter,includeEmptyFields,false);
	}
	

	public static Array call(PageContext pc , String list, String delimeter,boolean includeEmptyFields,boolean multiCharacterDelimiter) {
		if(includeEmptyFields){
			if(list.length()==0) {
				Array a=new ArrayImpl();
				a.appendEL("");
				return a;
			}
			return List.listToArray(list,delimeter,multiCharacterDelimiter);
		}
		if(list.length()==0) 
			return new ArrayImpl();
		
		return List.listToArrayRemoveEmpty(list,delimeter,multiCharacterDelimiter);
	}
}