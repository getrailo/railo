/**
 * Implements the Cold Fusion Function arrayappend
 */
package railo.runtime.functions.arrays;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;

/**
 * implementation of the Function arrayAppend
 */
public final class ArrayAppend implements Function {
	
	private static final long serialVersionUID = 5989673419120862625L;


	public static boolean call(PageContext pc , Array array, Object object) throws PageException {
		return call(pc, array, object, false);
	}
	
	/**
	 * @param pc
	 * @param array
	 * @param object
	 * @return has appended
	 * @throws PageException
	 */
	public static boolean call(PageContext pc , Array array, Object object, boolean merge) throws PageException {
		if(merge && Decision.isCastableToArray(object)) {
			Object[] appends = Caster.toNativeArray(object);
			for(int i=0;i<appends.length;i++){
				array.append(appends[i]);
			}
		}
		else
			array.append(object);
		return true;
	}
}