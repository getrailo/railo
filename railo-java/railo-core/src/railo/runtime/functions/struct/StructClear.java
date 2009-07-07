/**
 * Implements the Cold Fusion Function structclear
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Struct;

public final class StructClear implements Function {
	public static boolean call(PageContext pc , Struct struct) {
		struct.clear();
		return true;
	}
}