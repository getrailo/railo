package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public class SessionRotate implements Function {

	private static final long serialVersionUID = -114280474937883051L;

	public static String call(PageContext pc) throws PageException {
		((PageContextImpl)pc).invalidateUserScopes(true, true);
		return null;
	}

}
