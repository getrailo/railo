/**
 * Implements the CFML Function structkeyarray
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.util.CollectionUtil;

public final class StructKeyArray implements Function {
	public static Array call(PageContext pc , railo.runtime.type.Struct struct) {
		return KeyImpl.toArray(CollectionUtil.keys(struct));
	}
}