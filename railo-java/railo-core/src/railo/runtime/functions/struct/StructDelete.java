/**
 * Implements the Cold Fusion Function structdelete
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Struct;

public final class StructDelete implements Function {
	public static boolean call(PageContext pc , Struct struct, String key) {
		return call(pc , struct, key, false);
	}
	public static boolean call(PageContext pc , Struct struct, String key, boolean indicatenotexisting ) {
		return struct.removeEL(KeyImpl.init(key))!=null || !indicatenotexisting;
	}
}