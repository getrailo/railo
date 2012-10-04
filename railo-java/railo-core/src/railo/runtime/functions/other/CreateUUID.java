
package railo.runtime.functions.other;


import org.safehaus.uuid.UUIDGenerator;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

/**
 * Implements the CFML Function createuuid
 */
public final class CreateUUID implements Function {
	private static UUIDGenerator generator = UUIDGenerator.getInstance();
	
	/**
     * method to invoke the function
	 * @param pc
	 * @return UUID String
	 */
	public static String call(PageContext pc ) {
		return invoke();
	}
	public static String invoke() {
		String uuid = generator.generateRandomBasedUUID().toString().toUpperCase();
        return new StringBuffer(uuid.substring(0,23)).append(uuid.substring(24)).toString();
	}
}