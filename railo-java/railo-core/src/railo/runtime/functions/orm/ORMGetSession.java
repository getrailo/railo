package railo.runtime.functions.orm;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.orm.ORMSession;
import railo.runtime.orm.ORMUtil;

public class ORMGetSession extends BIF {
	public static Object call(PageContext pc) throws PageException {
		return call(pc, null);
	}

	public static Object call(PageContext pc, String datasource) throws PageException {
		ORMSession session = ORMUtil.getSession(pc);
		String dsn;
		if(StringUtil.isEmpty(datasource,true)) dsn=ORMUtil.getDataSource(pc).getName();
		else dsn=pc.getDataSource(datasource.trim()).getName();
		return session.getRawSession(dsn);
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==0) return call(pc);
		return call(pc,Caster.toString(args[0]));
	}
}
