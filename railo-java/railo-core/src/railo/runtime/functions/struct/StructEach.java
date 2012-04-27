/**
 * Implements the Cold Fusion Function arrayavg
 */
package railo.runtime.functions.struct;

import java.util.Iterator;
import java.util.Map.Entry;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Struct;
import railo.runtime.type.UDF;


public final class StructEach implements Function {

	private static final long serialVersionUID = 5795152568391831373L;

	public static String call(PageContext pc , Struct sct, UDF udf) throws PageException {
		Iterator<Entry<Key, Object>> it = sct.entryIterator();
		Entry<Key, Object> e;
		while(it.hasNext()){
			e = it.next();
			udf.call(pc, new Object[]{e.getKey().getString(),e.getValue()}, true);
		}
		return null;
	}
}