package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;

public class EntitySave {

	public static String call(PageContext pc, Object obj) throws PageException {
		return call(pc, obj, false);
	}
	public static String call(PageContext pc, Object obj,boolean forceInsert) throws PageException {
		ORMSession session=ORMUtil.getSession(pc);
		session.save(pc,obj,forceInsert);
		return null;
	}
}
