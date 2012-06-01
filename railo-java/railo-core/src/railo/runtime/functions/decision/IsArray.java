/**
 * Implements the CFML Function isarray
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Decision;
import railo.runtime.type.Array;

public final class IsArray implements Function {
	public static boolean call(PageContext pc , Object object) {
		return Decision.isArray(object);
	}
	public static boolean call(PageContext pc , Object object, double dimension) {
		if(dimension==-999) return Decision.isArray(object); // -999 == default value for named argument 
		
		
		if((object instanceof Array)) {
			return ((Array)object).getDimension()==(int)dimension;
		}
		else if(dimension==1) {
			return Decision.isArray(object);
		}
		return false;
	}
}