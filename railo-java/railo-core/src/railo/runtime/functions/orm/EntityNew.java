package railo.runtime.functions.orm;


import railo.runtime.Component;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;

public class EntityNew {

	public static Object call(PageContext pc, String name) throws PageException {
		return call(pc, name, null);
	}
	
	public static Object call(PageContext pc, String name,Struct properties) throws PageException {
		ORMSession session=ORMUtil.getSession(pc);
		if(properties==null)return session.create(pc,name);
		
		Component entity = session.create(pc,name);
		Key[] keys = properties.keys();
		for(int i=0;i<keys.length;i++){
			entity.call(pc, "set"+keys[i], new Object[]{properties.get(keys[i])});
		}
		return entity;
	}
}
