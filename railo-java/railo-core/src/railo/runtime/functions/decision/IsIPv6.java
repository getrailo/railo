package railo.runtime.functions.decision;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class IsIPv6 {
	public static boolean call(PageContext pc) throws PageException {
		try {
			InetAddress ia = InetAddress.getLocalHost();
			InetAddress[] ias = InetAddress.getAllByName(ia.getHostName());
			return _call(ias);
		} 
		catch (UnknownHostException e) {
			throw Caster.toPageException(e);
		}
	}
	
	public static boolean call(PageContext pc,String hostName) throws PageException {
		if(StringUtil.isEmpty(hostName)) return call(pc);
		try {
			InetAddress[] ias = InetAddress.getAllByName(hostName);
			return _call(ias);
		} 
		catch (UnknownHostException e) {
			if(hostName.equalsIgnoreCase("localhost") || hostName.equals("127.0.0.1") || hostName.equalsIgnoreCase("0:0:0:0:0:0:0:1") || hostName.equalsIgnoreCase("::1"))
	            return call(pc);
	        throw Caster.toPageException(e);
		}
	}
	
	
	private static boolean _call(InetAddress[] ias) {
		for(int i=0;i<ias.length;i++)	{
            if(ias[i] instanceof Inet6Address) return true;
        }
        return false;
	}
}
