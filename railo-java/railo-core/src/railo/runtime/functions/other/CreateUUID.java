
package railo.runtime.functions.other;

import java.util.UUID;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

/**
 * Implements the CFML Function createuuid
 */
public final class CreateUUID implements Function {

	/**
     * method to invoke the function
	 * @param pc
	 * @return UUID String
	 */
	public static String call(PageContext pc ) {
		return invoke();
	}
	public static String invoke() {
		String uuid = UUID.randomUUID().toString().toUpperCase();
        return new StringBuilder(uuid.substring(0,23)).append(uuid.substring(24)).toString();
	}
}