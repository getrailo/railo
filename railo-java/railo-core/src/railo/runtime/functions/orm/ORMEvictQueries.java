package railo.runtime.functions.orm;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;

public class ORMEvictQueries {
	public static String call(PageContext pc) throws PageException {
		return call(pc,null);
	}
	public static String call(PageContext pc,String cacheName) throws PageException {
		ORMSession session=ORMUtil.getSession(pc);
		if(StringUtil.isEmpty(cacheName))session.evictQueries(pc);
		else session.evictQueries(pc, cacheName);
		return null;
	}
}
