/**
 * Implements the CFML Function createodbcdate
 */
package railo.runtime.functions.dateTime;

import java.util.Calendar;
import java.util.TimeZone;

import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateImpl;
import railo.runtime.type.dt.DateTime;

public final class CreateODBCDate implements Function {

	private static final long serialVersionUID = -380258240258117961L;
	
	public static DateTime call(PageContext pc , DateTime datetime) {
		return call(pc, datetime,null);
	}
	public static DateTime call(PageContext pc , DateTime datetime, TimeZone tz) {
		if(tz==null) tz=((PageContextImpl)pc).getTimeZone();
		
		Calendar c = Calendar.getInstance(tz);
		c.setTime(datetime);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return new DateImpl(c.getTime());
	}
}