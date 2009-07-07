/**
 * Implements the Cold Fusion Function listinsertat
 */
package railo.runtime.functions.list;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class ListInsertAt implements Function {
	public static String call(PageContext pc , String list, double pos, String value) throws ExpressionException {
		return call(pc,list,pos,value,",");
	}

	public static String call(PageContext pc , String list, double posNumber, String value, String strDelimeter) throws ExpressionException {
		if(strDelimeter.length()==0)
        throw new FunctionException(pc,"listInsertAt",4,"delimeter","invalid delimeter value, can't be a empty string"); 
        
        return List.listInsertAt(list,(int)posNumber,value,strDelimeter);
	}
}