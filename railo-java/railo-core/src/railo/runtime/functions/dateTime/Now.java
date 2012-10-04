package railo.runtime.functions.dateTime;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

/**
 * Implements the CFML Function now
 */
public final class Now implements Function {
	/**
	 * @param pc
	 * @return
	 */
	public static DateTime call(PageContext pc ) {
		return new DateTimeImpl(pc);
	}
	
	/*public static DateTime now() {
		return new DateTimeImpl();
	}*/
}