/**
 * Implements the Cold Fusion Function structkeyexists
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;

public final class StructKeyExists implements Function {
	public static boolean call(PageContext pc , railo.runtime.type.Struct struct, String key) {
		return call(pc, struct, KeyImpl.init(key));
	}
	
	public static boolean call(PageContext pc , railo.runtime.type.Struct struct, Collection.Key key) {
		return struct.containsKey(key) && struct.get(key,null)!=null;
	}
}