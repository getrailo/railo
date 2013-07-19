/**
 * Implements the CFML Function gethttptimestring
 */
package railo.runtime.functions.dateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import railo.commons.date.DateTimeUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

public final class GetHttpTimeString implements Function {
	
	public static String call(PageContext pc) {
		return DateTimeUtil.toHTTPTimeString(new DateTimeImpl(pc));
	}

	public static String call(PageContext pc , DateTime datetime) {
        return DateTimeUtil.toHTTPTimeString(datetime==null?new DateTimeImpl(pc):datetime);
    }
	
	public static String invoke(DateTime datetime) {
        return DateTimeUtil.toHTTPTimeString(datetime);
	}
	
	public static String invoke() {
        return call(null);
	}
}