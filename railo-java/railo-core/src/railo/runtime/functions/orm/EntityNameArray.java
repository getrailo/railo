package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMEngine;
import railo.runtime.orm.ORMUtil;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;

public class EntityNameArray{
	
	public static Array call(PageContext pc) throws PageException {
		ORMEngine engine = ORMUtil.getSession(pc).getEngine();
		return new ArrayImpl(engine.getEntityNames());
	}
}
