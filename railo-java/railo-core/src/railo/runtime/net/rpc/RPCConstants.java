package railo.runtime.net.rpc;

import javax.xml.namespace.QName;

public final class RPCConstants {
	
	
	public static final QName COMPONENT = new QName("http://rpc.xml.coldfusion","Component");
	public static QName QUERY_QNAME=new QName("http://rpc.xml.coldfusion","QueryBean");
	public static QName ARRAY_QNAME=new QName("http://rpc.xml.coldfusion","Array");
    //private static QName componentQName=new QName("http://components.test.jm","address");
    //private static QName dateTimeQName=new QName("http://www.w3.org/2001/XMLSchema","dateTime");
    public static final QName STRING_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "string");
}
