/**
 * Implements the CFML Function dateconvert
 */
package railo.runtime.functions.dateTime;

import railo.runtime.PageContext;
import railo.runtime.exp.ApplicationException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;

public final class DateConvert implements Function {
	public static DateTime call(PageContext pc , String conversionType, DateTime date) throws ApplicationException {
		
		throw new ApplicationException("This function is no longer supported, because it gives you the wrong impression that the timezone is part of the date object, what is wrong!" +
				"When you wanna convert a Date to String based on the UTC timezone, do for example [DateTimeFormat(date:now(),timezone:'UTC')].");
		
		/*int offset = pc.getTimeZone().getOffset(date.getTime());
		conversionType=conversionType.toLowerCase();
		
		if(conversionType.equals("local2utc")) {
			return new DateTimeImpl(pc,date.getTime()-offset,false);
		}
		else if(conversionType.equals("utc2local")) {
			return new DateTimeImpl(pc,date.getTime()+offset,false);
		}		
		throw new ExpressionException("invalid conversion-type ["+conversionType+"] for function dateConvert");*/
	}
}