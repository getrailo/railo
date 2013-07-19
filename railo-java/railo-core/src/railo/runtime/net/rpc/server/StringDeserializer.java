package railo.runtime.net.rpc.server;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.ser.SimpleDeserializer;

public class StringDeserializer extends SimpleDeserializer {
	private static final Map<Pattern, String> replacements;
	static {
		replacements = new HashMap<Pattern, String>();
		for (char c = 0x00; c <= 0x1F; ++c) {
			replacements.put(Pattern.compile(StringSerializer.xmlCodeForChar(c)), Character.toString(c));
		}
		replacements.put(Pattern.compile(StringSerializer.xmlCodeForChar((char) 0x7F)), Character.toString((char) 0x7F));
	}

	public StringDeserializer(Class javaType, QName xmlType) {
		super(javaType, xmlType);
	}

	public Object makeValue(String source) throws Exception {
		String val = source; 

		for (Pattern pattern : replacements.keySet()) {
			val = pattern.matcher(val).replaceAll(replacements.get(pattern));
		}

		return val; 
	}
}
