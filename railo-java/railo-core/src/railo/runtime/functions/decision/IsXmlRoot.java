package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Decision;

/**
 * Implements the CFML Function isxmlroot
 */
public final class IsXmlRoot implements Function {
	public static boolean call(PageContext pc , Object object) {
		return Decision.isXMLRootElement(object);
	}
}