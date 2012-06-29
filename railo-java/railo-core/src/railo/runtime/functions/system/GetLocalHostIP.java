/**
 * Implements the CFML Function isarray
 */
package railo.runtime.functions.system;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class GetLocalHostIP implements Function {
	public static String call(PageContext pc)  {
		try {
            if(InetAddress.getLocalHost() instanceof Inet6Address) return "::1";
        }
        catch(UnknownHostException e) {}
        return "127.0.0.1";
	}
}