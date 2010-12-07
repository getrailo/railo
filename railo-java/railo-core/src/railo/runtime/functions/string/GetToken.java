/**
 * Implements the Cold Fusion Function gettoken
 */
package railo.runtime.functions.string;

import java.util.StringTokenizer;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.FunctionException;
import railo.runtime.ext.function.Function;

public final class GetToken implements Function {
	public static String call(PageContext pc , String str, double index) throws ExpressionException {
		return call(pc,str,index,null);
	}
	public static String call(PageContext pc , String str, double index, String delimeters) throws ExpressionException {
		if(delimeters==null) delimeters="\r\n\t ";
		
		if(index<1)
			throw new FunctionException(pc,"getToken",2,"index","index must be a positive number now ("+((int)index)+")");
		
		StringTokenizer tokens=new StringTokenizer(str,delimeters);
		int count=0;
		while(tokens.hasMoreTokens()) {
			if(++count==index)return tokens.nextToken();
			tokens.nextToken();
		}
		return "";
	}
}