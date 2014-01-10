package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;

public class EntityNameArray{
	
	public static Array call(PageContext pc) throws PageException {
		ORMSession sess = ORMUtil.getSession(pc);
		return new ArrayImpl(sess.getEntityNames());
	}
}
