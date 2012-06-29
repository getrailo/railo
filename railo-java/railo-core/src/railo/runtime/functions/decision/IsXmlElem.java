/**
 * Implements the CFML Function isxmlelem
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Decision;

public final class IsXmlElem implements Function {
	public static boolean call(PageContext pc , Object object) {
		return Decision.isXMLElement(object);
	}
}