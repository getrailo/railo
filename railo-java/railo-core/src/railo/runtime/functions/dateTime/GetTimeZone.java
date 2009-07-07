package railo.runtime.functions.dateTime;

import railo.commons.date.TimeZoneUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public class GetTimeZone implements Function{
	public static String call(PageContext pc) {
		return TimeZoneUtil.toString(pc.getTimeZone());
	}
		
}
