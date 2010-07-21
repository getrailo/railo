package railo.runtime.functions.orm;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;

public class ORMEvictEntity {
	public static String call(PageContext pc,String entityName) throws PageException {
		return call(pc, entityName,null);
	}
	public static String call(PageContext pc,String entityName,String primaryKey) throws PageException {
		ORMSession session=ORMUtil.getSession(pc);
		if(StringUtil.isEmpty(primaryKey))session.evictEntity(pc, entityName);
		else session.evictEntity(pc, entityName,primaryKey);
		return null;
	}
}
