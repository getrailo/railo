/**
 * Implements the CFML Function structkeyexists
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.query.QueryColumnExists;
import railo.runtime.type.Collection;
import railo.runtime.type.CollectionStruct;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;

public final class StructKeyExists implements Function {

	private static final long serialVersionUID = 7659087310641834209L;

	public static boolean call(PageContext pc , railo.runtime.type.Struct struct, String key) {
		return call(pc, struct, KeyImpl.init(key));
	}
	
	public static boolean call(PageContext pc , railo.runtime.type.Struct struct, Collection.Key key) {
		if(struct instanceof CollectionStruct) {
			Collection c=((CollectionStruct) struct).getCollection();
			if(c instanceof Query) {
				return QueryColumnExists.call(pc, (Query)c, key);
			}
		}
		return struct.containsKey(key); // NULL && struct.get(key,null)!=null;// do not change, this has do be this way
	}
}