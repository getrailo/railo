package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;

public class EntityDelete {
	public static String call(PageContext pc, Object obj) throws PageException {
		ORMSession session = ORMUtil.getSession(pc);
		session.delete(pc,obj);
		return null;
	}
}
