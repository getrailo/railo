/**
 * Implements the Cold Fusion Function structinsert
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.Collection.Key;

public final class StructInsert implements Function {
	public static boolean call(PageContext pc , Struct struct, String key, Object value) throws PageException {
		return call(pc , struct, key, value, false);
	}
	public static boolean call(PageContext pc , Struct struct, String strKey, Object value, boolean allowoverwrite) throws PageException {
		Key key = KeyImpl.init(strKey);
		if(allowoverwrite) { 
			struct.set(key,value);
		}
		else {
			if(struct.get(key,null)!=null) throw new ExpressionException("key ["+key+"] already exist in struct");
			struct.set(key,value);
		}
		return true;
	}
}