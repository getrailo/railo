/**
 * Implements the Cold Fusion Function arrayavg
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.UDF;


public final class ArrayEach implements Function {
	
	private static final long serialVersionUID = -2271260656749514177L;

	public static String call(PageContext pc , Array array, UDF udf) throws PageException {
		Key[] keys = array.keys();
		for(int i=0;i<keys.length;i++){
			udf.call(pc, new Object[]{array.get(keys[i])}, true);
		}
		return null;
	}
}