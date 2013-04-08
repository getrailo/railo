/**
 * Implements the CFML Function queryaddcolumn
 */
package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.db.SQLCaster;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.op.Caster;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;

public final class QueryAddColumn extends BIF {

	private static final long serialVersionUID = -242783888553490683L;

	public static double call(PageContext pc , Query query, String string) throws PageException {
		return call(pc, query, string,new ArrayImpl());
	}
	
	public static double call(PageContext pc , Query query, String string, Object array) throws PageException {
		query.addColumn(KeyImpl.init(string),Caster.toArray(array));
		return query.size();
	}
	
	public static double call(PageContext pc , Query query, String string, Object datatype, Object array) throws PageException {
		if(datatype==null) return call(pc, query, string, array);
		
		query.addColumn(KeyImpl.init(string),Caster.toArray(array),SQLCaster.toIntType(Caster.toString(datatype)));
		return query.size();
	}
	
	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if(args.length==2)return call(pc,Caster.toQuery(args[0]),Caster.toString(args[1]));
		if(args.length==3)return call(pc,Caster.toQuery(args[0]),Caster.toString(args[1]),args[2]);
		return call(pc,Caster.toQuery(args[0]),Caster.toString(args[1]),args[2],args[3]);
	}
}