/**
 * Implements the CFML Function getcurrenttemplatepath
 */
package railo.runtime.functions.system;

import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class GetCurrentTemplatePath implements Function {
	public static String call(PageContext pc ) throws ExpressionException {
		return ResourceUtil.getResource(pc, pc.getCurrentTemplatePageSource()).getAbsolutePath();
		//return pc.getCurrentTemplatePageSource().getPhyscalFile().getAbsolutePath();
	}
}