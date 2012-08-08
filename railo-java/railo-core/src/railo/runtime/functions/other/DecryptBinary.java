/**
 * Implements the CFML Function decrypt
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

//FUTURE 2 attr fehlen noch

public final class DecryptBinary implements Function {
	
    public synchronized static Object call(PageContext pc , Object oBytes, String key) throws PageException {
    	return call(pc,Caster.toBinary(oBytes),key,"cfmx_compat");
	}

    public synchronized static Object call(PageContext pc , Object oBytes, String key, String algorithm) throws PageException {
    	return Decrypt.invoke(Caster.toBinary(oBytes), key, algorithm); 
	}
}