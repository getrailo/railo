package railo.commons.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import railo.commons.lang.StringUtil;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ListUtil;

public class IPUtil {

	private static boolean isCacheEnabled = false;
	private static boolean isCacheValid = false;
	private static List<String> cachedLocalIPs = null;

	static {

		long tc = System.currentTimeMillis();

		List<String> localIPs = getLocalIPs( true );

		isCacheEnabled = System.currentTimeMillis() > tc;

		if ( isCacheEnabled ) {

			cachedLocalIPs = localIPs;
			isCacheValid = true;
		}
	}


	public static boolean isIPv4(String ip)	{
		String[] arr = ListUtil.trimItems(ListUtil.trim(ListUtil.listToStringArray(ip, '.')));
		if(arr.length!=4) return false;

		int tmp;
		for(int i=0;i<arr.length;i++){
			tmp=Caster.toIntValue(arr[i],-1);
			if(tmp<0 || tmp>255) return false;
		}
		return true;
	}

	public static boolean isIPv62(String ip)	{
		if(ip.indexOf(':') == -1) return false;
		String[] arr = ListUtil.trimItems(ListUtil.trim(ListUtil.listToStringArray(ip, ':')));
		if(arr.length!=8) return false;
		String str;
		int _int;
		for(int i=0;i<arr.length;i++){
			str=arr[i];
			if(!StringUtil.isEmpty(str)) {
				try{
					_int=Integer.parseInt(str,16);
				}
				catch(Throwable t){t.printStackTrace();
					_int=-1;
				}
				if(_int<0 || _int> 65535)
					return false;
			}
		}
		return true;
	}

	public static boolean isIPv4(InetAddress addr)	{
		return addr.getAddress().length==4;
	}
	public static boolean isIPv6(InetAddress addr)	{
		return !isIPv4(addr);
	}


	public static List<String> getLocalIPs( boolean refresh ) {

		if ( isCacheEnabled && isCacheValid && !refresh ) {

			return new ArrayList<String>( cachedLocalIPs );
		}

		List<String> result = new ArrayList();

		try {

			Enumeration<NetworkInterface> eNics = NetworkInterface.getNetworkInterfaces();

			while ( eNics.hasMoreElements() ) {

				NetworkInterface nic = eNics.nextElement();

				if ( nic.isUp() ) {

					Enumeration<InetAddress> eAddr = nic.getInetAddresses();

					while ( eAddr.hasMoreElements() ) {

						InetAddress inaddr = eAddr.nextElement();

						String addr = inaddr.toString();

						if ( addr.startsWith( "/" ) )
							addr = addr.substring( 1 );

						if ( addr.indexOf( '%' ) > -1 )
							addr = addr.substring( 0, addr.indexOf( '%' ) );    // internal zone in some IPv6; http://en.wikipedia.org/wiki/IPv6_Addresses#Link-local%5Faddresses%5Fand%5Fzone%5Findices

						result.add( addr );
					}
				}
			}
		}
		catch ( SocketException e ) {

			result.add( "127.0.0.1" );
			result.add( "0:0:0:0:0:0:0:1" );
		}

		if ( isCacheEnabled ) {

			cachedLocalIPs = result;
			isCacheValid = true;
		}

		return result;
	}


	/** this method can be called from Controller periodically, or from Admin if user clicks to invalidate the cache */
	public void invalidateCache() {

		isCacheValid = false;
	}


	/** returns true if cache is used */
	public boolean isCacheEnabled() {

		return isCacheEnabled;
	}

}
