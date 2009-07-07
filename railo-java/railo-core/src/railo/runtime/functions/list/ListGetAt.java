/**
 * Implements the Cold Fusion Function listgetat
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class ListGetAt implements Function {
	
    public static String call(PageContext pc , String list, double pos) throws PageException {
		return call(pc,list,pos,",");
	}
	public static String call(PageContext pc , String list, double posNumber, String delimeter) throws PageException {
		int pos=(int) posNumber;
		String rtn = List.getAt(list,delimeter,pos-1);
		if(rtn==null)
		    throw new FunctionException(pc,"listGetAt",2,"posNumber","invalid string list index ["+pos+"]");
		return rtn;
	}
}