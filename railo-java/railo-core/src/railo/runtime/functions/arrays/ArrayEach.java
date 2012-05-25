/**
 * Implements the ColdFusion Function arrayAvg
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.closure.Each;
import railo.runtime.type.Array;
import railo.runtime.type.UDF;


public final class ArrayEach implements Function {
	
	private static final long serialVersionUID = -2271260656749514177L;

	public static String call(PageContext pc , Array array, UDF udf) throws PageException {
		Each.invoke(pc, array, udf);
		return null;
	}
}