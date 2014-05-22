package railo.runtime.functions.dateTime;

import java.util.TimeZone;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public class SetTimeZone implements Function{
	public static TimeZone call(PageContext pc,TimeZone tz) {
		TimeZone old = pc.getTimeZone();
		pc.setTimeZone(tz);
		return old;
	}
		
}