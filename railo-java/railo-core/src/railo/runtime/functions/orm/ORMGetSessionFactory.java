package railo.runtime.functions.orm;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.db.DataSource;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.orm.ORMEngine;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;

public class ORMGetSessionFactory extends BIF {
	
	public static Object call(PageContext pc) throws PageException {
		return call(pc, null);
	}
	
	public static Object call(PageContext pc, String datasource) throws PageException {
		ORMSession session = ORMUtil.getSession(pc);
		
		DataSource ds;
		if(StringUtil.isEmpty(datasource,true)) ds=ORMUtil.getDataSource(pc);
		else ds=((PageContextImpl)pc).getDataSource(datasource.trim());
		
		return session.getRawSessionFactory(ds);
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==0) return call(pc);
		return call(pc,Caster.toString(args[0]));
	}
}
