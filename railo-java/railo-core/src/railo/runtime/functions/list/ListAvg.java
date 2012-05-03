/**
 * Implements the Cold Fusion Function arrayavg
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;
import railo.runtime.type.util.ArrayUtil;


public final class ListAvg implements Function {
    public static double call(PageContext pc , String list) throws ExpressionException {
        return call(pc,list,",");
    }
    public static double call(PageContext pc , String list, String delimiter) throws ExpressionException {
        return ArrayUtil.avg(List.listToArrayRemoveEmpty(list,delimiter));
    }
}