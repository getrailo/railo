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
		return call(pc,str,null,null);
	}
    public synchronized static String call(PageContext pc , String string, String algorithm) throws PageException {
		return call(pc,string,algorithm,null);
	}
    public synchronized static String call(PageContext pc , String string, String algorithm, String encoding) throws PageException {
		return invoke(pc.getConfig(), string, algorithm, encoding);
	}

    public synchronized static String invoke(Config config, String string, String algorithm, String encoding) throws PageException {
		if(StringUtil.isEmpty(algorithm))algorithm="md5";
		else algorithm=algorithm.trim().toLowerCase();
		if(StringUtil.isEmpty(encoding))encoding=config.getWebCharset();
		
		
		try {
			if("md5".equals(algorithm) || "cfmx_compat".equals(algorithm)) 
				return Md5.getDigestAsString(string).toUpperCase();
			
			MessageDigest md=MessageDigest.getInstance(algorithm);
		    md.reset();
		    md.update(string.getBytes(encoding));
		    return Md5.stringify(md.digest()).toUpperCase();
		} 
		catch (Exception e) {
			throw Caster.toPageException(e);
		}
	}

}