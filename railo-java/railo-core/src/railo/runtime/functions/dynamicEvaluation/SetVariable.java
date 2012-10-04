/**
 * Implements the CFML Function setvariable
 */
package railo.runtime.functions.dynamicEvaluation;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class SetVariable implements Function {
	public static Object call(PageContext pc , String name, Object value) throws PageException {
		
	    
	    return pc.setVariable(name,value);//pc.setVariable(name,value);
	}
}