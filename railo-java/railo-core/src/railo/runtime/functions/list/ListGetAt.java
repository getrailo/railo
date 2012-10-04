/**
 * Implements the CFML Function listgetat
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class ListGetAt implements Function {
	
	private static final long serialVersionUID = -8227074223983816122L;

	public static String call(PageContext pc , String list, double posNumber) throws PageException {
		return call(pc,list,posNumber,",",false);
	}

	public static String call(PageContext pc , String list, double posNumber, String delimiter) throws PageException {
		return call(pc,list,posNumber,delimiter,false);
	}
	
	public static String call(PageContext pc , String list, double posNumber, String delimiter, boolean includeEmptyFields) throws PageException {
		int pos=(int) posNumber;
		String rtn = List.getAt(list,delimiter,pos-1,!includeEmptyFields);
		if(rtn==null) throw new FunctionException(pc,"listGetAt",2,"posNumber","invalid string list index ["+pos+"]");
		return rtn;
	}
}