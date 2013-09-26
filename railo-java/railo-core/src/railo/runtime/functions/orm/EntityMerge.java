package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;

public class EntityMerge {

	public static Object call(PageContext pc, Object obj) throws PageException {
		ORMSession session=ORMUtil.getSession(pc);
		return session.merge(pc,obj);
	}
}
