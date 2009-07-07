/**
 * Implements the Cold Fusion Function structupdate
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class StructUpdate implements Function {
	public static boolean call(PageContext pc , railo.runtime.type.Struct struct, String key, Object object) throws PageException {
		struct.get(key);
		struct.set(key,object);
		return true;
	}
}