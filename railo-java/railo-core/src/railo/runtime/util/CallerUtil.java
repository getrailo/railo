package railo.runtime.util;

import railo.runtime.PageContext;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Null;

public class CallerUtil {
	public static Object get(PageContext pc,Object coll, Key[] keys, Object defaultValue) {
		if(coll==null) return defaultValue;
		int to=keys.length-1;
		for(int i=0;i<=to;i++){
			coll=((VariableUtilImpl)pc.getVariableUtil()).getCollection(pc, coll, keys[i], Null.NULL);
			if(coll==Null.NULL || (coll==null && i<to)) return defaultValue;
		}
		return coll;
	}
}
