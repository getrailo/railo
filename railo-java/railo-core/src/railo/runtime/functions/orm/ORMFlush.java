package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;

public class ORMFlush {
	public static String call(PageContext pc) throws PageException {
		ORMSession session = ORMUtil.getSession(pc);
		session.flush(pc);
		return null;
	}
}
