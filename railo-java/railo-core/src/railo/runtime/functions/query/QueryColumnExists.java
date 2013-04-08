package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;

public class QueryColumnExists extends BIF {

	private static final long serialVersionUID = -661796711105724696L;

	public static boolean call(PageContext pc , Query qry, String key) {
		return call(pc,qry,KeyImpl.getInstance(key));
	}
	public static boolean call(PageContext pc , Query qry, Collection.Key key) {
		return qry.getColumn(key,null)!=null;
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toQuery(args[0]),Caster.toKey(args[1]));
	}
}
