/**
 * Implements the CFML Function monthasstring
 */
package railo.runtime.functions.string;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class MonthShortAsString implements Function {
	public static String call(PageContext pc , double month) throws ExpressionException {
		switch((int) month) {
			case 1: return "Jan";
			case 2: return "Feb";
			case 3: return "Mar";
			case 4: return "Apr";
			case 5: return "May";
			case 6: return "Jun";
			case 7: return "Jul";
			case 8: return "Aug";
			case 9: return "Sep";
			case 10: return "Oct";
			case 11: return "Nov";
			case 12: return "Dec";
			default: throw new ExpressionException("invalid month definition in function monthShortAsString, must be between 1 and 12 now ["+month+"]");
		}
	
	}
}