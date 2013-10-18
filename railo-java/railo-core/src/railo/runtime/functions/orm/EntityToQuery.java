package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;
import railo.runtime.type.Query;

public class EntityToQuery {
	
	public static Query call(PageContext pc, Object obj) throws PageException {
		return call(pc, obj,null);
	}
	
	public static Query call(PageContext pc, Object obj, String name) throws PageException {
		ORMSession session=ORMUtil.getSession(pc);
		return session.toQuery(pc,obj,name);
		
	}
}
