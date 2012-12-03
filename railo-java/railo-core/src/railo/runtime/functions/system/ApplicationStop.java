package railo.runtime.functions.system;

import railo.runtime.CFMLFactoryImpl;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.type.scope.ScopeContext;

public class ApplicationStop {
	public static String call(PageContext pc) throws PageException {
		CFMLFactoryImpl factory = (CFMLFactoryImpl)pc.getCFMLFactory();
		ScopeContext sc = factory.getScopeContext();
		sc.clearApplication(pc);
		return null;
	}
}
