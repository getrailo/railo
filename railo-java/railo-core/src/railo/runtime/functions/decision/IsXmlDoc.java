/**
 * Implements the CFML Function isxmldoc
 */
package railo.runtime.functions.decision;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Decision;

public final class IsXmlDoc implements Function {
	public static boolean call(PageContext pc , Object object) {
		return Decision.isXMLDocument(object);
	}
}