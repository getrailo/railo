/**
 * Implements the Cold Fusion Function encrypt
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.coder.Coder;
import railo.runtime.crypt.CFMXCompat;
import railo.runtime.crypt.Cryptor;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

// FUTURE 2 attr fehlen noch

public final class Encrypt implements Function {
    
    // "CFMX_COMPAT" "UU"  null, 0
    public synchronized static String call(PageContext pc , String string, String key) throws PageException {
		return call(pc,string,key,"cfmx_compat","uu");
	}
    
    public synchronized static String call(PageContext pc , String string, String key, String algorithm) throws PageException {
		return call(pc,string,key,algorithm,"uu"); 
	}
    
    public synchronized static String call(PageContext pc , String string, String key, String algorithm, String encoding) throws PageException {
    	return invoke(string, key, algorithm, encoding);
	}
    
    public synchronized static String invoke(String input, String key, String algorithm, String encoding) throws PageException {
    	try {
			return Coder.encode(encoding,invoke(input.getBytes("UTF-8"), key, algorithm));
		} 
    	catch (Exception e) {
			throw Caster.toPageException(e);
		}
    }
    
    public synchronized static byte[] invoke(byte[] input, String key, String algorithm) throws PageException {
    	try {
    		if(algorithm==null || "cfmx_compat".equalsIgnoreCase(algorithm)){
    			return new CFMXCompat().transformString(key, input);
    		}
    		return Cryptor.encrypt(algorithm, key, input);	
		} 
    	catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}
}