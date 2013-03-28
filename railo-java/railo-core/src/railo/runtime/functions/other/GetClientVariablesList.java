/**
 * Implements the CFML Function getclientvariableslist
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.util.ListUtil;

public final class GetClientVariablesList implements Function {

	private static final long serialVersionUID = 5352798254941551343L;

	public static String call(PageContext pc ) throws PageException {
		return ListUtil.arrayToList((pc.clientScope()).pureKeys(),",");
	}
}