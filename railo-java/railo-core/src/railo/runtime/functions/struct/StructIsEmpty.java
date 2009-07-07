/**
 * Implements the Cold Fusion Function structisempty
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Struct;

public final class StructIsEmpty implements Function {
	public static boolean call(PageContext pc , Struct struct) {
		return struct.size()==0;
	}
}