/**
 * Implements the CFML Function createodbctime
 */
package railo.runtime.functions.dateTime;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.TimeImpl;

public final class CreateODBCTime implements Function {
	public static DateTime call(PageContext pc , DateTime datetime) {
		return new TimeImpl(datetime);
	}
}