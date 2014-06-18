package railo.runtime.functions.orm;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.orm.ORMUtil;

public class ORMGetSessionFactory extends BIF {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8739815940242857106L;

	public static Object call(PageContext pc) throws PageException {
		return call(pc, null);
	}
	
	public static Object call(PageContext pc, String datasource) throws PageException {
		String dsn=ORMUtil.getDataSource(pc, datasource).getName();
		return ORMUtil.getSession(pc).getRawSessionFactory(dsn);
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==0) return call(pc);
		return call(pc,Caster.toString(args[0]));
	}
}
