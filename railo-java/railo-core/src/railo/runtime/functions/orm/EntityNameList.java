package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMEngine;
import railo.runtime.orm.ORMUtil;
import railo.runtime.type.List;

public class EntityNameList {

	public static String call(PageContext pc) throws PageException {
		return call(pc,",");
	}
	
	public static String call(PageContext pc, String delimiter) throws PageException {
		ORMEngine engine = ORMUtil.getSession(pc).getEngine();
		return List.arrayToList(engine.getEntityNames(),delimiter);
	}
}
