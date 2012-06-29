package railo.runtime.functions.displayFormatting;

import railo.runtime.PageContext;
import railo.runtime.exp.FunctionNotSupported;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

/**
 * Implements the CFML Function AjaxLink
 */
public final class AjaxLink implements Function {
	
	public static String call(PageContext pc , String url) throws PageException {
		throw new FunctionNotSupported("AjaxLink");
	}
}