package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;

public class EntityNew {

	public static Object call(PageContext pc, String name) throws PageException {
		ORMSession session=ORMUtil.getSession(pc);
		//ORMEngine engine = ORMUtil.getEngine(pc);
		return session.create(pc,name);
	}
}
