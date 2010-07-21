package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMEngine;
import railo.runtime.orm.ORMUtil;

public class ORMGetSessionFactory {
	public static Object call(PageContext pc) throws PageException {
		ORMEngine engine= ORMUtil.getEngine(pc);
		return engine.getSessionFactory(pc);
	}
}
