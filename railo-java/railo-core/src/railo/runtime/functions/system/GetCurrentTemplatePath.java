/**
 * Implements the CFML Function getcurrenttemplatepath
 */
package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class GetCurrentTemplatePath implements Function {

	private static final long serialVersionUID = 1862733968548626803L;

	public static String call(PageContext pc ) throws PageException {
		return pc.getCurrentTemplatePageSource().getResourceTranslated(pc).getAbsolutePath();
	}
}