package railo.runtime.functions.orm;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMUtil;

public class ORMFlush {
	public static String call(PageContext pc) throws PageException {
		return call(pc, null);
	}
	public static String call(PageContext pc, String datasource) throws PageException {
		if(StringUtil.isEmpty(datasource,true)) ORMUtil.getSession(pc).flush(pc);
		else ORMUtil.getSession(pc).flush(pc,datasource.trim());
		return null;
	}
}
