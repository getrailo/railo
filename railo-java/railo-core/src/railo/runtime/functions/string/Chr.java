/**
 * Implements the CFML Function chr
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class Chr implements Function {
	public static String call(PageContext pc , double number) throws ExpressionException {
		int value=(int) number;
		if(value<1){
			if(value==0)return "";
			//else {
				throw new ExpressionException("Parameter 1 of function chr which is now ["+value+"] must be a non-negative integer");
			//}
		}
		return ""+(char)value;		
	}
}