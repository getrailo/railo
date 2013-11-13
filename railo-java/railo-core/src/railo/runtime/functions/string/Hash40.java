/**
 * Implements the CFML Function hash
 */
package railo.runtime.functions.string;

import java.security.MessageDigest;

import railo.commons.lang.MD5Legacy;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class Hash40 implements Function {
	
	// function for old code in ra files calling this function
	public static String call(PageContext pc, String input) throws PageException {
		return invoke( pc.getConfig(), input, null, null, 1 );
	}
    public static String call(PageContext pc , String input, String algorithm) throws PageException {
		return invoke( pc.getConfig(), input, algorithm, null, 1 );
	}
    public static String call(PageContext pc , String input, String algorithm, String encoding) throws PageException {
		return invoke( pc.getConfig(), input, algorithm, encoding, 1 );
	}
	//////
	
	
	public static String call(PageContext pc, Object input) throws PageException {
		return invoke( pc.getConfig(), input, null, null, 1 );
	}
	
    public static String call(PageContext pc , Object input, String algorithm) throws PageException {
		return invoke( pc.getConfig(), input, algorithm, null, 1 );
	}

    public static String call(PageContext pc , Object input, String algorithm, String encoding) throws PageException {
		return invoke( pc.getConfig(), input, algorithm, encoding, 1 );
	}
    
    public static String call(PageContext pc , Object input, String algorithm, String encoding, double numIterations) throws PageException {
		return invoke( pc.getConfig(), input, algorithm, encoding, (int)numIterations );
	}

    /*/	this method signature was called from ConfigWebAdmin.createUUID(), comment this comment to enable
    public synchronized static String invoke(Config config, Object input, String algorithm, String encoding) throws PageException {
    	
    	return invoke(config, input, algorithm, encoding, 1);
    }	//*/
    
    public static String invoke(Config config, Object input, String algorithm, String encoding, int numIterations) throws PageException {
		
    	if(StringUtil.isEmpty(algorithm))algorithm="md5";
		else algorithm=algorithm.trim().toLowerCase();
		if(StringUtil.isEmpty(encoding))encoding=config.getWebCharset();
		
		boolean isDefaultAlgo = numIterations == 1 && ("md5".equals(algorithm) || "cfmx_compat".equals(algorithm));
		byte[] arrBytes = null;
		
		try {			
			if(input instanceof byte[]) {
				arrBytes = (byte[])input;
				if(isDefaultAlgo) return MD5Legacy.getDigestAsString( arrBytes ).toUpperCase();
			} 
			else {
				String string = Caster.toString(input);
				if (isDefaultAlgo) return MD5Legacy.getDigestAsString( string ).toUpperCase();
				arrBytes = string.getBytes( encoding );
			}
			
			MessageDigest md=MessageDigest.getInstance(algorithm);
		    md.reset();
		    
			for(int i=0; i<numIterations; i++)
				md.update(arrBytes);
		    
			return MD5Legacy.stringify( md.digest() ).toUpperCase();
		} 
		catch (Throwable t) {
			throw Caster.toPageException(t);
		}
	}

}