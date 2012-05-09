package railo.runtime.functions.system;

import railo.print;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public class SessionInvalidate implements Function {

	private static final long serialVersionUID = -4219932267580490719L;

	public static String call(PageContext pc) throws PageException {
		print.e(pc.sessionScope().hashCode());
		((PageContextImpl)pc).invalidateSession();
		return null;
	}

}
