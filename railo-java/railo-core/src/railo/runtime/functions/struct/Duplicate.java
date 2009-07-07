/**
 * Implements the Cold Fusion Function duplicate
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Duplicator;

public final class Duplicate implements Function {
	public static Object call(PageContext pc , Object object) {
		return Duplicator.duplicate(object,true);
	}
	public static Object call(PageContext pc , Object object,boolean deepCopy) {
		return Duplicator.duplicate(object,deepCopy);
	}
	
	
	
}