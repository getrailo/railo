package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;
import railo.runtime.type.util.KeyConstants;

/**
 * returns the root of this actuell Page Context
 */
public final class GetCurrentContext implements Function {
	
	public static Array call(PageContext pc) {
		Array arr=new ArrayImpl();
		CallStackGet._getTagContext(pc, arr, new Exception("Stack trace"),KeyConstants._line);
		return arr;
	}
}