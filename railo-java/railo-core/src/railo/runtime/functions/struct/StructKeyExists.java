/**
 * Implements the Cold Fusion Function structkeyexists
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class StructKeyExists implements Function {
	public static boolean call(PageContext pc , railo.runtime.type.Struct struct, String key) {
		return struct.containsKey(key);
	}
}