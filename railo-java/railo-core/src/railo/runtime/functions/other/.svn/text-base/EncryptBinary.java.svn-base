/**
 * Implements the Cold Fusion Function encrypt
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

//FUTURE 2 attr fehlen noch

public final class EncryptBinary implements Function {
	
    public synchronized static Object call(PageContext pc , Object oBytes, String key) throws PageException {
		return call(pc,oBytes,key,"cfmx_compat");
	}
    
    public synchronized static Object call(PageContext pc , Object oBytes, String key, String algorithm) throws PageException {
		return Encrypt.invoke(Caster.toBinary(oBytes), key, algorithm);
    	
	}
    
}