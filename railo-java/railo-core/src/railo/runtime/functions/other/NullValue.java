/**
 * Implements the CFML Function NullValue
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class NullValue implements Function {
    
    public synchronized static Object call(PageContext pc) {
        return null;
    }
    
    
}