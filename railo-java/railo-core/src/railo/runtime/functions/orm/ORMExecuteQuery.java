package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.Struct;

public class ORMExecuteQuery {
	
	public static Object call(PageContext pc,String hql) throws PageException {
		return _call(pc,hql,null,false,null);	
	}
	public static Object call(PageContext pc,String hql,Object paramsOrUnique) throws PageException {
		if(Decision.isCastableToBoolean(paramsOrUnique)){
			return _call(pc,hql,null,Caster.toBooleanValue(paramsOrUnique),null);
		}
		return _call(pc,hql,paramsOrUnique,false,null);
	}
	public static Object call(PageContext pc,String hql,Object paramsOrUnique,Object uniqueOrQueryOptions) throws PageException {
		if(Decision.isCastableToBoolean(paramsOrUnique)){
			return _call(pc,hql,null,Caster.toBooleanValue(paramsOrUnique),Caster.toStruct(uniqueOrQueryOptions));
		}
		if(Decision.isCastableToBoolean(uniqueOrQueryOptions)){
			return _call(pc,hql,paramsOrUnique,Caster.toBooleanValue(uniqueOrQueryOptions),null);
		}
		return _call(pc,hql,paramsOrUnique,false,Caster.toStruct(uniqueOrQueryOptions));
	}

	public static Object call(PageContext pc,String hql,Object params,Object unique, Object queryOptions) throws PageException {
		return _call(pc,hql,params,Caster.toBooleanValue(unique),Caster.toStruct(queryOptions));
	}
	private static Object _call(PageContext pc,String hql, Object params, boolean unique, Struct queryOptions) throws PageException {
		ORMSession session=ORMUtil.getSession(pc);
		if(params==null)
			return session.executeQuery(pc,hql,new ArrayImpl(),unique,queryOptions);
		else if(Decision.isStruct(params))
			return session.executeQuery(pc,hql,Caster.toStruct(params),unique,queryOptions);
		else if(Decision.isArray(params))
			return session.executeQuery(pc,hql,Caster.toArray(params),unique,queryOptions);
		else if(Decision.isCastableToStruct(params))
			return session.executeQuery(pc,hql,Caster.toStruct(params),unique,queryOptions);
		else if(Decision.isCastableToArray(params))
			return session.executeQuery(pc,hql,Caster.toArray(params),unique,queryOptions);
		else
			throw new FunctionException(pc, "ORMExecuteQuery", 2, "params", "cannot convert the params to a array or a struct");
	}
}
