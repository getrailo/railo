package railo.runtime.net.rpc.server;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.ser.SimpleSerializer;
import org.apache.commons.lang.StringUtils;

public class StringSerializer extends SimpleSerializer {
	protected static String xmlCodeForChar(char c) {
		StringBuilder buff = new StringBuilder();
		buff.append("&#x");
		buff.append(StringUtils.leftPad(Integer.toHexString(c), 4, "0"));
		buff.append(";"); 
		
		return buff.toString();
	}

    public StringSerializer(Class javaType, QName xmlType) {
		super(javaType, xmlType);
	}

	public String getValueAsString(Object value, SerializationContext context) {
        return escapeNonPrintableChars(super.getValueAsString(value, context));
    }

	private String escapeNonPrintableChars(String val) {
		StringBuilder buff = new StringBuilder();
		for (int idx = 0; idx < val.length(); ++idx) {
			char c = val.charAt(idx);
			buff.append(charIsNonPrintable(c) ? xmlCodeForChar(c) : c);
		}
		
		return buff.toString(); 
	}

	private boolean charIsNonPrintable(char c) {
		//0x00 to 0x1F and 0x7F are ASCII control characters
		return c < 0x1F || c == 0x7F;
	}
}
