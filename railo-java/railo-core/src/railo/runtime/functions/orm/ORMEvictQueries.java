package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMUtil;

public class ORMEvictQueries {
	public static String call(PageContext pc) throws PageException {
		return call(pc,null,null);
	}
	
	public static String call(PageContext pc,String cacheName) throws PageException {
		return call(pc,cacheName,null);
	}
	
	public static String call(PageContext pc,String cacheName,String datasource) throws PageException {
		ORMUtil.getSession(pc).evictQueries(pc,cacheName,datasource);
		return null;
	}
}
