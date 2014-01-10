package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;
import railo.runtime.type.util.ListUtil;

public class EntityNameList {

	public static String call(PageContext pc) throws PageException {
		return call(pc,",");
	}
	
	public static String call(PageContext pc, String delimiter) throws PageException {
		ORMSession sess = ORMUtil.getSession(pc);
		return ListUtil.arrayToList(sess.getEntityNames(),delimiter);
	}
}
