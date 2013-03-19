/**
 * Implements the CFML Function isarray
 */
package railo.runtime.functions.system;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Array;
import railo.runtime.type.ArrayImpl;


public final class GetLocalHostIP implements Function {

	public static Object call(PageContext pc)  {
		try {
            if(InetAddress.getLocalHost() instanceof Inet6Address) return "::1";
        }
        catch(UnknownHostException e) {}
        return "127.0.0.1";
	}


    public static Object call(PageContext pc, boolean all) throws PageException {

        if ( !all )
            return call(pc);

        Array result = new ArrayImpl();

        try {

            List<String> addresses = getAllLocalAddresses();

            for ( String addr : addresses )
                result.append(addr);
        }
        catch(SocketException e) {

            result.append( "0:0:0:0:0:0:0:1" );
            result.append( "127.0.0.1" );
        }

//      result.append( "::1" );      // should we add ::1 ?

        return result;
    }


    static List<String> getAllLocalAddresses() throws SocketException {

        List<String> result = new ArrayList();

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

        return result;
    }

}