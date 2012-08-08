/**
 * Implements the CFML Function directoryexists
 */
package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionNotSupported;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class DotNetToCFType implements Function {
	public static Object call(PageContext pc , Object input) throws PageException {
		throw new FunctionNotSupported("DotNetToCFType");
	}
}