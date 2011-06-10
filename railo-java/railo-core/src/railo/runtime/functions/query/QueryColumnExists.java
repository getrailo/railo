package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;

public class QueryColumnExists {
	public static boolean call(PageContext pc , Query qry, String key) {
		return call(pc,qry,KeyImpl.init(key));
	}
	public static boolean call(PageContext pc , Query qry, Collection.Key key) {
		return qry.getColumn(key,null)!=null;
	}
}
