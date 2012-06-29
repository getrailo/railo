/**
 * Implements the CFML Function insert
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class Insert implements Function {
    public static String call(PageContext pc , String sub, String str, double pos) throws ExpressionException {
		int p=(int) pos;
		if(p<0 || p>str.length())
			throw new ExpressionException("third parameter of the function insert, must be between 0 and "+str.length()+" now ["+(p)+"]");
		StringBuffer sb=new StringBuffer(str.length()+sub.length());
		
		return sb.append(str.substring(0,p)).append(sub).append(str.substring(p)).toString();
	}
}