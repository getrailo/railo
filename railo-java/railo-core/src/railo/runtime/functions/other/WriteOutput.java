/**
 * Implements the CFML Function writeoutput
 */
package railo.runtime.functions.other;

import java.io.IOException;

import railo.runtime.PageContext;
import railo.runtime.exp.NativeException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public final class WriteOutput implements Function {
    public static boolean call(PageContext pc , String string) throws PageException {
        try {
            pc.forceWrite(string);
        } catch (IOException e) {
            throw new NativeException(e);
        }
        return true;
    }
}