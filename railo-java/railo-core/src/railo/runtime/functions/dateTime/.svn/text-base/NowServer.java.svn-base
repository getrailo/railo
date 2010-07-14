package railo.runtime.functions.dateTime;

import java.util.TimeZone;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

/**
 * Implements the Cold Fusion Function now
 * @deprecated removed with no replacement
 */
public final class NowServer implements Function {
	/**
	 * @param pc
	 * @return server time
	 * @throws ExpressionException 
	 */
	public static DateTime call(PageContext pc ) throws ExpressionException {
		//throw new ExpressionException("function nowServer is deprecated");
		long now = System.currentTimeMillis();
		int railo = pc.getTimeZone().getOffset(now);
		int server = TimeZone.getDefault().getOffset(now);
		
		return new DateTimeImpl(pc,now-(railo-server),false);
		
	}
	
}