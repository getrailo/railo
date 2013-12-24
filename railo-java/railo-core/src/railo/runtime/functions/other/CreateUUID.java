
package railo.runtime.functions.other;


import org.apache.commons.codec.binary.Base64;
import org.safehaus.uuid.UUIDGenerator;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

import java.math.BigInteger;

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
	public static String call(PageContext pc) {
		return invoke(false);
	}

	public static String call(PageContext pc, boolean b) {
		return invoke(b);
	}

	public static String invoke() {
		return invoke(false);
	}

	public static String invoke(boolean isShort) {

		String uuid = generator.generateRandomBasedUUID().toString();

		if (isShort) {

			uuid = (new StringBuilder(32))
					.append( uuid.substring( 0,  8) )
					.append( uuid.substring( 9, 13) )
					.append( uuid.substring(14, 18) )
					.append( uuid.substring(19, 23) )
					.append( uuid.substring(24) ).toString();

			// can be converted back with: new BigInteger( Base64.decodeBase64( shortUUIDString ) ).toString(16) and adding the hyphen delimiters
			return Base64.encodeBase64URLSafeString( (new BigInteger(uuid, 16)).toByteArray() );
		}

		return (new StringBuilder(36))
				.append( uuid.substring( 0, 23) )
				.append( uuid.substring(24) ).toString().toUpperCase();
	}
}