package railo.runtime.functions.owasp;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.errors.EncodingException;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public class encodeForXPath implements Function{
	public static String call(PageContext pc , String item){
		Encoder encoder = ESAPI.encoder();
		return encoder.encodeForXPath(item);		
	}

}
