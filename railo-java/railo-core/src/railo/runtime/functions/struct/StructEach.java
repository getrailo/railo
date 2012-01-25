/**
 * Implements the Cold Fusion Function arrayavg
 */
package railo.runtime.functions.struct;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;


public final class StructEach implements Function {

	private static final long serialVersionUID = 5795152568391831373L;

	public static String call(PageContext pc , Struct sct, UDF udf) throws PageException {
		Key[] keys = sct.keys();
		for(int i=0;i<keys.length;i++){
			udf.call(pc, new Object[]{keys[i],sct.get(keys[i])}, true);
		}
		return null;
	}
}