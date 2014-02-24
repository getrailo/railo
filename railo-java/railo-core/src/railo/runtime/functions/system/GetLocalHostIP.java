/**
 * Implements the CFML Function GetLocalHostIP
 */
package railo.runtime.functions.system;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import railo.commons.net.IPUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class GetLocalHostIP implements Function {

	public static Object call(PageContext pc)  {
		return callLegacy();
	}


	public static Object call(PageContext pc, boolean all, boolean refresh) {

		if ( all )
			return IPUtil.getLocalIPs( refresh );

		return callLegacy();
	}


	public static Object call(PageContext pc, boolean all) {

		return call( pc, all, false );
	}


	static String callLegacy() {

		try {
			if(InetAddress.getLocalHost() instanceof Inet6Address) return "::1";
		}
		catch(UnknownHostException e) {}
		return "127.0.0.1";
	}
}