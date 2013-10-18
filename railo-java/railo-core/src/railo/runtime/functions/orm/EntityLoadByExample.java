package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;

public class EntityLoadByExample {
	public static Object call(PageContext pc, Object sampleEntity) throws PageException {
		return call(pc, sampleEntity, false);
	}
	
	public static Object call(PageContext pc, Object sampleEntity,boolean unique) throws PageException {
		ORMSession session=ORMUtil.getSession(pc);
		if(unique)return session.loadByExample(pc,sampleEntity);
		return session.loadByExampleAsArray(pc,sampleEntity);
	}
}
