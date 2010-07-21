package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;

public class EntityLoadByPK {
	public static Object call(PageContext pc, String name,String id) throws PageException {
		//return EntityLoad.call(pc, name, id, Boolean.TRUE);
		
		ORMSession session=ORMUtil.getSession(pc);
		//ORMEngine engine= ORMUtil.getEngine(pc);
		return session.load(pc,name, id);
	}
}
