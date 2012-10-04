package railo.runtime.functions.displayFormatting;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionNotSupported;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

/**
 * Implements the CFML Function AjaxLink
 */
public final class AjaxOnLoad implements Function {
	
	public static String call(PageContext pc , String functionName) throws PageException {
		throw new FunctionNotSupported("AjaxOnLoad");
	}
}