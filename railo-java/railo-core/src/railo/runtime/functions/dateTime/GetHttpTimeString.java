/**
 * Implements the CFML Function gethttptimestring
 */
package railo.runtime.functions.dateTime;

import railo.commons.date.DateTimeUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

public final class GetHttpTimeString implements Function {
	
	public static String call(PageContext pc) {
		return DateTimeUtil.toHTTPTimeString(new DateTimeImpl(pc),true);
	}

	public static String call(PageContext pc , DateTime datetime) {
        return DateTimeUtil.toHTTPTimeString(datetime==null?new DateTimeImpl(pc):datetime,true);
    }
	
	public static String invoke(DateTime datetime) {
        return DateTimeUtil.toHTTPTimeString(datetime,true);
	}
	
	public static String invoke() {
        return call(null);
	}
}