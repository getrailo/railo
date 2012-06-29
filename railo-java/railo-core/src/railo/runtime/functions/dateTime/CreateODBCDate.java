/**
 * Implements the CFML Function createodbcdate
 */
package railo.runtime.functions.dateTime;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateImpl;
import railo.runtime.type.dt.DateTime;

public final class CreateODBCDate implements Function {
	public static DateTime call(PageContext pc , DateTime datetime) {
		return new DateImpl(datetime);
	}
}