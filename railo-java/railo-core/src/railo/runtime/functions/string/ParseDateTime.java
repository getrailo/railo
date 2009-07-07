package railo.runtime.functions.string;

import java.util.TimeZone;

import railo.commons.date.TimeZoneUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.date.DateCaster;

/**
 * Implements the Cold Fusion Function parsedatetime
 */
public final class ParseDateTime implements Function {
	public static railo.runtime.type.dt.DateTime call(PageContext pc , Object oDate) throws PageException {
		return _call(oDate,pc.getTimeZone());
	}
	public static railo.runtime.type.dt.DateTime call(PageContext pc , Object oDate, String string2) throws PageException {
		return _call(oDate,pc.getTimeZone());
	}
	public static railo.runtime.type.dt.DateTime call(PageContext pc , Object oDate, String string2,String strTimezone) throws PageException {
		return _call(oDate,TimeZoneUtil.toTimeZone(strTimezone));
	}
	
	private static railo.runtime.type.dt.DateTime _call( Object oDate,TimeZone tz) throws PageException {
		return DateCaster.toDateAdvanced(oDate,tz);
	}
}