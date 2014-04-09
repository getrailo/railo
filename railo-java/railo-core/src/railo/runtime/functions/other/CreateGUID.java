
package railo.runtime.functions.other;

import java.util.UUID;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

/**
 * Implements the CFML Function createGuid
 */
public final class CreateGUID implements Function {

    public static String call(PageContext pc ) {
        return  UUID.randomUUID().toString().toUpperCase();
    }
}