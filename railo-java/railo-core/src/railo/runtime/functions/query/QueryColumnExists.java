package railo.runtime.functions.query;

import railo.runtime.PageContext;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;

public class QueryColumnExists {
	public static boolean call(PageContext pc , Query qry, String key) {
		return qry.getColumn(KeyImpl.init(key),null)!=null;
	}
}
