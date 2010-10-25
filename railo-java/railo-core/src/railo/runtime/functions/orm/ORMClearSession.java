package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMUtil;

public class ORMClearSession {

	public static String call(PageContext pc) throws PageException {
		ORMUtil.getSession(pc).clear(pc);
		return null;
	}
}
