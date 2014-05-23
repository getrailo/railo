package railo.runtime.functions.string;

import java.util.TimeZone;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.date.DateCaster;

/**
 * Implements the CFML Function parsedatetime
 */
public final class ParseDateTime implements Function {

	private static final long serialVersionUID = -2623323893206022437L;
	
	public static railo.runtime.type.dt.DateTime call(PageContext pc , Object oDate) throws PageException {
		return _call(oDate,pc.getTimeZone());
	}
	public static railo.runtime.type.dt.DateTime call(PageContext pc , Object oDate, String popConversion) throws PageException {
		return _call(oDate,pc.getTimeZone());
	}
	public static railo.runtime.type.dt.DateTime call(PageContext pc , Object oDate, String popConversion,TimeZone tz) throws PageException {
		return _call(oDate,tz==null?pc.getTimeZone():tz);
	}
	private static railo.runtime.type.dt.DateTime _call( Object oDate,TimeZone tz) throws PageException {
		return DateCaster.toDateAdvanced(oDate,DateCaster.CONVERTING_TYPE_YEAR,tz);
	}
}