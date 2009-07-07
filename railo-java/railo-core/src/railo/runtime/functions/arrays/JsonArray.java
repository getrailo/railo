package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;

public class JsonArray implements Function {
	/**
	 * @param pc
	 * @param objArr
	 * @return
	 * @throws ExpressionException
	 */
	public static Array call(PageContext pc , Object[] objArr) {
		return Array_.call(pc, objArr);
	}
}
