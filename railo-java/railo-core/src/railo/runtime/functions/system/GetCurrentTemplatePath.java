/**
 * Implements the Cold Fusion Function getcurrenttemplatepath
 */
package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class GetCurrentTemplatePath implements Function {
	public static String call(PageContext pc ) {
		return pc.getCurrentTemplatePageSource().getPhyscalFile().getAbsolutePath();
	}
}