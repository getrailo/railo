/**
 * Implements the CFML Function hash
 */
package railo.runtime.functions.string;

import java.security.MessageDigest;

import railo.commons.lang.Md5;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class Hash implements Function {
	
	public static String call(PageContext pc , String str) throws PageException {
		return call(pc,str,null,null, 1);
	}
	
    public synchronized static String call(PageContext pc , String string, String algorithm) throws PageException {
		return call(pc,string,algorithm,null, 1);
	}

    public synchronized static String call(PageContext pc , String string, String algorithm, String encoding) throws PageException {
		return invoke(pc.getConfig(), string, algorithm, encoding, 1);
	}
    
    public synchronized static String call(PageContext pc , String string, String algorithm, String encoding, int numIterations) throws PageException {
		return invoke(pc.getConfig(), string, algorithm, encoding, numIterations);
	}

    /*/	this method signature was called from ConfigWebAdmin.createUUID(), comment this comment to enable
    public synchronized static String invoke(Config config, Object input, String algorithm, String encoding) throws PageException {
    	
    	return invoke(config, input, algorithm, encoding, 1);
    }	//*/
    
    public synchronized static String invoke(Config config, Object input, String algorithm, String encoding, int numIterations) throws PageException {
		
    	if(StringUtil.isEmpty(algorithm))algorithm="md5";
		else algorithm=algorithm.trim().toLowerCase();
		if(StringUtil.isEmpty(encoding))encoding=config.getWebCharset();
		
		boolean isDefaultAlgo = numIterations == 1 && ("md5".equals(algorithm) || "cfmx_compat".equals(algorithm));
		byte[] arrBytes = null;
		
		try {			

			if ( input instanceof byte[] ) {
				
				arrBytes = (byte[])input;
				
				if ( isDefaultAlgo ) 
					return Md5.stringify( arrBytes );
			} else {
				
				String string = input.toString();
				
				if ( isDefaultAlgo ) 
					return Md5.getDigestAsString( string ).toUpperCase();
				
				arrBytes = string.getBytes( encoding );
			}
			
			MessageDigest md=MessageDigest.getInstance(algorithm);
		    
			md.reset();
		    
			for( int i=0; i<numIterations; i++ )
				md.update( arrBytes );
		    
			return Md5.stringify( md.digest() ).toUpperCase();
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}

}