package railo.runtime.functions.decision;

import java.net.InetAddress;
import java.net.UnknownHostException;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

/**
 * Implements the Cold Fusion Function isleapyear
 */
public final class IsLocalHost implements Function {
	public static boolean call(PageContext pc , String ip) {
		
		if(StringUtil.isEmpty(ip,true)) return false;
		ip=ip.trim().toLowerCase();
		if("localhost".equalsIgnoreCase(ip) || "127.0.0.1".equals(ip)) return true;
		
		try {
        	return InetAddress.getLocalHost().equals(InetAddress.getByName(ip));
        }
        catch(UnknownHostException e){
            return false;
        }
	}

}