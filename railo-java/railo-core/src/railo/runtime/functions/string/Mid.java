/**
 * Implements the Cold Fusion Function mid
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class Mid implements Function {
	public static String call(PageContext pc , String str, double start) throws ExpressionException {
		return call(pc,str,start,str.length());
	}
	public static String call(PageContext pc , String str, double start, double count) throws ExpressionException {
		int s=(int) (start-1);
		int c=(int) count;
		
		if(s<0) throw new ExpressionException("Parameter 2 of function mid which is now ["+(s+1)+"] must be a positive integer");
		if(c<0) throw new ExpressionException("Parameter 3 of function mid which is now ["+c+"] must be a non-negative integer");
		c+=s;
		if(s>str.length()) return "";
		else if(c>=str.length())return str.substring(s);
		else {
			return str.substring(s,c);
		}
	}
}