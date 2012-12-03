/**
 * Implements the CFML Function deleteclientvariable
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.KeyImpl;

public final class DeleteClientVariable implements Function {
	public static boolean call(PageContext pc , String variableName) throws PageException {
		return pc.clientScope().removeEL(KeyImpl.init(variableName))!=null;
	}
}