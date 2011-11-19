package railo.runtime.functions.owasp;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public class encodeForXML implements Function  {
	public static String call(PageContext pc , String item)  {
		Encoder encoder = ESAPI.encoder();
		return encoder.encodeForXML(item);		
	}
}
