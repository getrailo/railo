package railo.runtime.functions.orm;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;

public class ORMEvictCollection {
	public static String call(PageContext pc,String entityName,String collectionName) throws PageException {
		return call(pc, entityName, collectionName,null);
	}
	public static String call(PageContext pc,String entityName,String collectionName,String primaryKey) throws PageException {
		ORMSession session = ORMUtil.getSession(pc);
		if(StringUtil.isEmpty(primaryKey))session.evictCollection(pc, entityName, collectionName);
		else session.evictCollection(pc, entityName, collectionName,primaryKey);
		return null;
	}
}
