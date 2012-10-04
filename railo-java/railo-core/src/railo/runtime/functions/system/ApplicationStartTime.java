package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.dt.DateTimeImpl;

public class ApplicationStartTime implements Function {

	private static final long serialVersionUID = -3051854049187102667L;

	public static DateTime call(PageContext pc) throws PageException {
		return new DateTimeImpl(pc.applicationScope().getCreated(),false);
	}
}
