package railo.runtime.net.rpc.server;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.encoding.Serializer;

import org.apache.axis.encoding.ser.SimpleSerializerFactory;

public class StringSerializerFactory extends SimpleSerializerFactory {
	public StringSerializerFactory(Class javaType, QName xmlType) {
		super(javaType, xmlType);
	}

	public Serializer getSerializerAs(String mechanismType) throws JAXRPCException {
        if (javaType == String.class) {
            return new StringSerializer(javaType, xmlType);
        }

        return super.getSerializerAs(mechanismType);
    }
}
