package railo.runtime.functions.dateTime;

import java.util.TimeZone;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public class GetTimeZone implements Function{

	private static final long serialVersionUID = 2953112893625358220L;

	public static TimeZone call(PageContext pc) {
		return pc.getTimeZone();
	}
		
}
