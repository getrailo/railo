package railo.runtime.net.rpc.server;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.Deserializer;

import org.apache.axis.encoding.ser.SimpleDeserializerFactory;

public class StringDeserializerFactory extends SimpleDeserializerFactory {
	public StringDeserializerFactory(Class javaType, QName xmlType) {
		super(javaType, xmlType);
	}
	
	public Deserializer getDeserializerAs(String mechanismType) {
		if (javaType == String.class) {
			return new StringDeserializer(javaType, xmlType);
		}
		
		return super.getDeserializerAs(mechanismType);
	}
}
