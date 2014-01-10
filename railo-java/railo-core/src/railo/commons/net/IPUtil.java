package railo.commons.net;

import java.net.InetAddress;

import railo.commons.lang.StringUtil;
import railo.runtime.op.Caster;
import railo.runtime.type.util.ListUtil;

public class IPUtil {


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
    
    /*public static void main(String[] args) throws UnknownHostException {
    	long start=System.currentTimeMillis();
    	print.o(isIPv4(InetAddress.getByName("localhost")));
    	print.o(isIPv4(InetAddress.getByName("0.0.0.0")));
    	print.o(isIPv4(InetAddress.getByName("127.0.0.1")));
    	print.o(isIPv4(InetAddress.getByName("255.255.255.255")));
		print.o(isIPv6(InetAddress.getByName("0:0:0:0:0:0:0:1%0")));
		print.o(System.currentTimeMillis()-start);
	}*/

}
