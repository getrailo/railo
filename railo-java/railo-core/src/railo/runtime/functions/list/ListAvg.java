/**
 * Implements the CFML Function arrayavg
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.util.ArrayUtil;
import railo.runtime.type.util.ListUtil;


public final class ListAvg implements Function {
    public static double call(PageContext pc , String list) throws ExpressionException {
        return call(pc,list,",");
    }
    public static double call(PageContext pc , String list, String delimiter) throws ExpressionException {
        return ArrayUtil.avg(ListUtil.listToArrayRemoveEmpty(list,delimiter));
    }
}