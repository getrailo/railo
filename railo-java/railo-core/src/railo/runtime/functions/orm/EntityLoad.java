package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public class EntityLoad {
	
	public static Object call(PageContext pc, String name) throws PageException {
		
		ORMSession session=ORMUtil.getSession(pc);
		return session.loadAsArray(pc,name,new StructImpl());
	}
	
	public static Object call(PageContext pc, String name,Object idOrFilter) throws PageException {
		return call(pc, name, idOrFilter, Boolean.FALSE);
	}
	public static Object call(PageContext pc, String name,Object idOrFilter, Object uniqueOrOptions) throws PageException {
		ORMSession session=ORMUtil.getSession(pc);
		
		// id
		if(Decision.isSimpleValue(idOrFilter)){
			// id,unique
			if(Decision.isCastableToBoolean(uniqueOrOptions)){
				// id,unique=true
				if(Caster.toBooleanValue(uniqueOrOptions))
					return session.load(pc,name, Caster.toString(idOrFilter));
				// id,unique=false
				return session.loadAsArray(pc,name, Caster.toString(idOrFilter));
			}
			else if(Decision.isString(uniqueOrOptions)){
				return session.loadAsArray(pc,name,Caster.toString(idOrFilter),Caster.toString(uniqueOrOptions));
			}
			
			// id,options
			return session.loadAsArray(pc,name,Caster.toString(idOrFilter));
		}
		
		// filter,[unique|sortorder]
		if(Decision.isSimpleValue(uniqueOrOptions)){
			// filter,unique
			if(Decision.isBoolean(uniqueOrOptions)){
				if(Caster.toBooleanValue(uniqueOrOptions))
					return session.load(pc,name,Caster.toStruct(idOrFilter));
				return session.loadAsArray(pc,name,Caster.toStruct(idOrFilter));
			}
			// filter,sortorder
			return session.loadAsArray(pc,name,Caster.toStruct(idOrFilter),(Struct)null,Caster.toString(uniqueOrOptions));
		}
		// filter,options
		return session.loadAsArray(pc,name,Caster.toStruct(idOrFilter),Caster.toStruct(uniqueOrOptions));
	}
	
	
	
	public static Object call(PageContext pc, String name,Object filter, Object order, Object options) throws PageException {
		ORMSession session=ORMUtil.getSession(pc);
		return session.loadAsArray(pc,name,Caster.toStruct(filter),Caster.toStruct(options),Caster.toString(order));
	}
}
