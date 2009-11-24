
package eu.xlogics.rdirect_asp360.xtrace.ws.xtrace.asmx.webservices.com.microsoft;

import railo.runtime.net.rpc.Pojo;

public class __GetEventByReferenceNoResponse_GetEventByReferenceNoResult2170
    implements Pojo
{
    public static final String _md5_ = "baa8fb05815443015b6c4e31b0405573";
    private Object any;
    
    public Object getAny() {
	return any;
    }
    
    public void setAny(Object any) {
	this.any = any;
    }
    

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(__GetEventByReferenceNoResponse_GetEventByReferenceNoResult2170.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://microsoft.com/webservices/", ">>GetEventByReferenceNoResponse>GetEventByReferenceNoResult"));
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
    	return typeDesc;
    }
}
