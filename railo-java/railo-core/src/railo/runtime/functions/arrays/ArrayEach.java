/**
 * Implements the ColdFusion Function arrayAvg
 */
package railo.runtime.functions.arrays;

import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.UDF;


public final class ArrayEach implements Function {
	
	private static final long serialVersionUID = -2271260656749514177L;

	public static String call(PageContext pc , Array array, UDF udf) throws PageException {
		Iterator<Object> it = array.valueIterator();
		while(it.hasNext()){
			udf.call(pc, new Object[]{it.next()}, true);
		}
		return null;
	}
}