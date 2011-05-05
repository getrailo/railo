/**
 * Implements the Cold Fusion Function structkeyexists
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.decision.IsDefined;
import railo.runtime.type.Collection;

public final class StructKeyExists implements Function {
	public static boolean call(PageContext pc , railo.runtime.type.Struct struct, String key) {
		return struct.get(key,null)!=null;
	}
	
	public static boolean call(PageContext pc , railo.runtime.type.Struct struct, Collection.Key key) {
		return struct.get(key,null)!=null;
	}
}