package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;

public class EntityLoadByPK {
	public static Object call(PageContext pc, String name,String id) throws PageException {
		ORMSession session=ORMUtil.getSession(pc);
		return session.load(pc,name, id);
	}
}
