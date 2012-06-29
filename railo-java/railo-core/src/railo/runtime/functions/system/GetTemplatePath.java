/**
 * Implements the CFML Function gettemplatepath
 */
package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class GetTemplatePath implements Function {
	public static Object call(PageContext pc) throws PageException {
		
        return pc.getTemplatePath();
	}
}