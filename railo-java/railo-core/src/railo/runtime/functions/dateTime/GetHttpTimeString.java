/**
 * Implements the Cold Fusion Function gethttptimestring
 */
package railo.runtime.functions.dateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

public final class GetHttpTimeString implements Function {
	
	private final static SimpleDateFormat HTTP_TIME_STRING_FORMAT;
	static {
		HTTP_TIME_STRING_FORMAT = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss zz",Locale.ENGLISH);
		HTTP_TIME_STRING_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	
	private static synchronized String toHTTPTimeString(Date date) {
		return StringUtil.replace(HTTP_TIME_STRING_FORMAT.format(date),"+00:00","",true);
	}
	
	
	public static String call(PageContext pc) {
		return toHTTPTimeString(new DateTimeImpl(pc));
	}

	public static String call(PageContext pc , DateTime datetime) {
        return toHTTPTimeString(datetime);
    }
	
	public static String invoke(DateTime datetime) {
        return toHTTPTimeString(datetime);
	}
	
	public static String invoke() {
        return call(null);
	}
}