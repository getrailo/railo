/**
 * Implements the CFML Function arrayavg
 */
package railo.runtime.functions.closure;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.type.Array;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.Iteratorable;
import railo.runtime.type.UDF;


public final class Each implements Function {

	private static final long serialVersionUID = 1955185705863596525L;

	public static String call(PageContext pc , Object obj, UDF udf) throws PageException {
		// Array
		if(obj instanceof Array) {
			invoke(pc, (Array)obj, udf);
		}
		// other Iteratorable
		else if(obj instanceof Iteratorable) {
			invoke(pc, (Iteratorable)obj, udf);
		}
		// Map
		else if(obj instanceof Map) {
			Iterator it = ((Map)obj).entrySet().iterator();
			Entry e;
			while(it.hasNext()){
				e = (Entry) it.next();
				udf.call(pc, new Object[]{e.getKey(),e.getValue()}, true);
			}
		}
		//List
		else if(obj instanceof List) {
			Iterator it = ((List)obj).iterator();
			while(it.hasNext()){
				udf.call(pc, new Object[]{it.next()}, true);
			}
		}
		// Iterator
		else if(obj instanceof Iterator) {
			Iterator it = (Iterator)obj;
			while(it.hasNext()){
				udf.call(pc, new Object[]{it.next()}, true);
			}
		}
		// Enumeration
		else if(obj instanceof Enumeration) {
			Enumeration e = (Enumeration)obj;
			while(e.hasMoreElements()){
				udf.call(pc, new Object[]{e.nextElement()}, true);
			}
		}
		else
			throw new FunctionException(pc, "Each", 1, "data", "cannot iterate througth this type "+Caster.toTypeName(obj.getClass()));
		
		return null;
	}
	

	public static void invoke(PageContext pc , Array array, UDF udf) throws PageException {
		Iterator<Object> it = array.valueIterator();
		while(it.hasNext()){
			udf.call(pc, new Object[]{it.next()}, true);
		}
	}

	public static void invoke(PageContext pc , Iteratorable coll, UDF udf) throws PageException {
		Iterator<Entry<Key, Object>> it = coll.entryIterator();
		Entry<Key, Object> e;
		while(it.hasNext()){
			e = it.next();
			udf.call(pc, new Object[]{e.getKey().getString(),e.getValue()}, true);
		}
	}
	
}