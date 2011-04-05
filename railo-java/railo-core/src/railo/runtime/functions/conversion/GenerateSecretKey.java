package railo.runtime.functions.conversion;


import javax.crypto.KeyGenerator;

import railo.runtime.PageContext;
import railo.runtime.coder.Coder;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * Generates a Secret Key
 */
public final class GenerateSecretKey implements Function {
	
	public static String call(PageContext pc, String algorithm) throws PageException {
		return call(pc, algorithm,0);
	}
	
	public static String call(PageContext pc, String algorithm, double keySize) throws PageException {
		try	{
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm.toUpperCase());
            if(keySize>0) keyGenerator.init(Caster.toIntValue(keySize));
            return Coder.encode(Coder.ENCODING_BASE64, keyGenerator.generateKey().getEncoded());
        }
        catch(Exception e) {
            throw Caster.toPageException(e);
        }
	}
	
}