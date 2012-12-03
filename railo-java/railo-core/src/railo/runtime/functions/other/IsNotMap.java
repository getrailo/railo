/**
 * Implements the CFML Function isnotmap
 */
package railo.runtime.functions.other;

import java.util.Map;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class IsNotMap implements Function {
	public static boolean call(PageContext pc , Object object) {
		return !(object instanceof Map);
	}
}