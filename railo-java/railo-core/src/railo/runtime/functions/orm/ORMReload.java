package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMUtil;

public class ORMReload {
	public static String call(PageContext pc) throws PageException {
		ORMUtil.resetEngine(pc);
		return null;
	}
}
