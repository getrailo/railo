/**
 * Implements the CFML Function structkeyexists
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.config.NullSupportHelper;
import railo.runtime.exp.PageException;
import railo.runtime.functions.BIF;
import railo.runtime.functions.query.QueryColumnExists;
import railo.runtime.op.Caster;
import railo.runtime.type.Collection;
import railo.runtime.type.CollectionStruct;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;

public final class StructKeyExists extends BIF {

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
		if(NullSupportHelper.full()) return struct.containsKey(key);
		
		return struct.containsKey(key) && struct.get(key,null)!=null;// do not change, this has do be this way
	}

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		return call(pc,Caster.toStruct(args[0]),Caster.toKey(args[1]));
	}
}