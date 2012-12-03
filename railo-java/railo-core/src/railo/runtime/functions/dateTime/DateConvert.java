/**
 * Implements the CFML Function dateconvert
 */
package railo.runtime.functions.dateTime;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

public final class DateConvert implements Function {
	public static DateTime call(PageContext pc , String conversionType, DateTime date) throws ExpressionException {
		int offset = pc.getTimeZone().getOffset(date.getTime());
		conversionType=conversionType.toLowerCase();
		
		if(conversionType.equals("local2utc")) {
			return new DateTimeImpl(pc,date.getTime()-offset,false);
		}
		else if(conversionType.equals("utc2local")) {
			return new DateTimeImpl(pc,date.getTime()+offset,false);
		}		
		throw new ExpressionException("invalid conversion-type ["+conversionType+"] for function dateConvert");
	}
}