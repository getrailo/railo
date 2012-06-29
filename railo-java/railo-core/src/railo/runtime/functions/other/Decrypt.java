/**
 * Implements the CFML Function decrypt
 */
package railo.runtime.functions.other;

import railo.runtime.PageContext;
import railo.runtime.coder.Coder;
import railo.runtime.crypt.CFMXCompat;
import railo.runtime.crypt.Cryptor;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

//FUTURE 2 attr fehlen noch

public final class Decrypt implements Function {
    
    
    public synchronized static String call(PageContext pc , String string, String key) throws PageException {
    	return call(pc,string,key,"cfmx_compat","uu");
	}

    public synchronized static String call(PageContext pc , String string, String key, String algorithm) throws PageException {
    	return call(pc,string,key,algorithm,"uu");
	}
    public synchronized static String call(PageContext pc , String string, String key, String algorithm, String encoding) throws PageException {
    	return invoke(string, key, algorithm, encoding);
	}
    

    protected synchronized static String invoke(String string, String key, String algorithm, String encoding) throws PageException {
    	try {
			return new String(invoke(Coder.decode(encoding, string), key, algorithm),"UTF-8");
		} 
    	catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}
    protected synchronized static byte[] invoke(byte[] input, String key, String algorithm) throws PageException {
    	try {
    		if("cfmx_compat".equalsIgnoreCase(algorithm)){
	    		return new CFMXCompat().transformString(key, input);
	    	}
	    	return Cryptor.decrypt(algorithm, key, input);	
		} 
    	catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}
}