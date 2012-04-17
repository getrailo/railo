package railo.runtime.net.rpc;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.rpc.encoding.TypeMappingRegistry;

import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;

import coldfusion.xml.rpc.QueryBean;

public class TypeMappingUtil {

	public static void registerDefaults(TypeMappingRegistry tmr) { 
		TypeMapping tm = tmr.getDefaultTypeMapping();
		if(!tm.isRegistered(QueryBean.class, RPCConstants.QUERY_QNAME))
			tm.register(QueryBean.class, 
                RPCConstants.QUERY_QNAME,
                new BeanSerializerFactory(QueryBean.class,RPCConstants.QUERY_QNAME),
                new BeanDeserializerFactory(QueryBean.class,RPCConstants.QUERY_QNAME));
	}
	
	public static void registerBeanTypeMapping(javax.xml.rpc.encoding.TypeMapping tm, Class clazz, QName qName) {
		
		if(tm.isRegistered(clazz, qName)) return;
		tm.register(
    			clazz, 
        		qName, 
    			new BeanSerializerFactory(clazz, qName), 
    			new BeanDeserializerFactory(clazz, qName));
		
		
	}
	
}
