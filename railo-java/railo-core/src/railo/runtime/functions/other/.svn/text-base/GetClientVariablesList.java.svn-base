/**
 * Implements the Cold Fusion Function getclientvariableslist
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.List;

public final class GetClientVariablesList implements Function {
	public static String call(PageContext pc ) throws PageException {
		return List.arrayToList((pc.clientScope()).pureKeys(),",");
	}
}