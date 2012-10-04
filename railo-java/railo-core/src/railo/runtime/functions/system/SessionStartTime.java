package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

public class SessionStartTime implements Function {

	private static final long serialVersionUID = -3620067950402419075L;

	public static DateTime call(PageContext pc) throws PageException {
		return new DateTimeImpl(pc.sessionScope().getCreated(),false);
	}
}
