/**
 * Implements the Cold Fusion Function structkeyexists
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class ListIndexExists implements Function {
   
	private static final long serialVersionUID = 7642583305678735361L;
	
	public static boolean call(PageContext pc , String list, double index) {
        return call(pc,list,index,",",false);
    }
    public static boolean call(PageContext pc , String list, double index, String delimeter) {
        return call(pc,list,index,delimeter,false);
    }
    public static boolean call(PageContext pc , String list, double index, String delimeter,boolean includeEmptyFields) {
        if(includeEmptyFields)return List.listToArray(list,delimeter).get((int)index,null)!=null;
    	return List.listToArrayRemoveEmpty(list,delimeter).get((int)index,null)!=null;
    }
}