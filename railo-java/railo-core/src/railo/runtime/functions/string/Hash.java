/**
 * Implements the CFML Function hash
 */
package railo.runtime.functions.string;

import java.security.MessageDigest;

import railo.commons.digest.HashUtil;
import railo.commons.lang.StringUtil;
import railo.commons.lang.SystemOut;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class Hash implements Function {
	
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
    	if("cfmx_compat".equals(algorithm)) algorithm="md5";
    	else if("quick".equals(algorithm)) {
    		if(numIterations>1) 
    			SystemOut.printDate("for algorithm [quick], argument numIterations makes no sense, because this algorithm has no security in mind");
    		return HashUtil.create64BitHashAsString(Caster.toString(input), 16);
    	}
    	
    	
		
    	if(StringUtil.isEmpty(encoding))encoding=config.getWebCharset();
		byte[] data = null;
		
		try {			
			if(input instanceof byte[]) data = (byte[])input;
			else data = Caster.toString(input).getBytes( encoding );
			
			MessageDigest md=MessageDigest.getInstance(algorithm);
		    md.reset();
		    for(int i=0; i<numIterations; i++) {
		    	data=md.digest(data);
			}
		    return railo.commons.digest.Hash.toHexString(data,true);
		} 
		catch (Throwable t) {
			throw Caster.toPageException(t);
		}
	}

}