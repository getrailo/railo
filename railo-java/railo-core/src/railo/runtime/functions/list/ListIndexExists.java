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
    public static boolean call(PageContext pc , String list, double index, String delimiter) {
        return call(pc,list,index,delimiter,false);
    }
    public static boolean call(PageContext pc , String list, double index, String delimiter,boolean includeEmptyFields) {
        if(includeEmptyFields)return List.listToArray(list,delimiter).get((int)index,null)!=null;
    	return List.listToArrayRemoveEmpty(list,delimiter).get((int)index,null)!=null;
    }
}