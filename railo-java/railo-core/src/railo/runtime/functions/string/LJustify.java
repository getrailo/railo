/**
 * Implements the CFML Function ljustify
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class LJustify implements Function {
	public static String call(PageContext pc , String str, double length) throws ExpressionException {
		int len=(int) length;
		if(len<1) throw new ExpressionException("Parameter 2 of function lJustify which is now ["+len+"] must be a positive integer");
		else if((len-=str.length())<=0) return str;
		else {
			StringBuffer sb=new StringBuffer(str.length()+len);
			sb.append(str);
			for(int i=1;i<=len;i++) {
				//str+=" ";
				sb.append(' ');
			}
			return sb.toString();
		}
	}
}