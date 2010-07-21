/**
 * Implements the Cold Fusion Function structkeyexists
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class ListIndexExists implements Function {
    public static boolean call(PageContext pc , String list, double index) {
        return call(pc,list,index,",");
    }
    public static boolean call(PageContext pc , String list, double index, String delimeter) {
        return List.listToArrayRemoveEmpty(list,delimeter).get((int)index,null)!=null;
    }
}