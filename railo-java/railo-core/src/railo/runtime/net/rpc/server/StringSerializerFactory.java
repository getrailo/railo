package railo.runtime.net.rpc.server;

import org.apache.axis.encoding.ser.SimpleSerializerFactory;

import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.encoding.Serializer;

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
