
package railo.runtime.functions.other;


import org.safehaus.uuid.UUIDGenerator;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

/**
 * Implements the CFML Function createGuid
 */
public final class CreateGUID implements Function {
    private static UUIDGenerator generator = UUIDGenerator.getInstance();
    
    public static String call(PageContext pc ) {
        return  generator.generateRandomBasedUUID().toString().toUpperCase();
    }
}