package railo.runtime.functions.string;

import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
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
	public static railo.runtime.type.dt.DateTime call(PageContext pc , Object oDate, String popConversion,String strTimezone) throws PageException {
		return _call(oDate,strTimezone==null?pc.getTimeZone():TimeZoneUtil.toTimeZone(strTimezone));
	}
	private static railo.runtime.type.dt.DateTime _call( Object oDate,TimeZone tz) throws PageException {
		// MUSt implement popConversion
		return DateCaster.toDateAdvanced(oDate,tz);
	}
}