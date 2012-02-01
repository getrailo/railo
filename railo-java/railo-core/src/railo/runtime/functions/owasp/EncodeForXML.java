package railo.runtime.functions.owasp;

import java.io.PrintStream;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;

import railo.commons.io.DevNullOutputStream;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public class encodeForXML implements Function  {
	public static String call(PageContext pc , String item)  {
		PrintStream out = System.out;
		try {
			 System.setOut(new PrintStream(DevNullOutputStream.DEV_NULL_OUTPUT_STREAM));
			 Encoder encoder = ESAPI.encoder();
			 return encoder.encodeForXML(item);	
		}
		finally {
			 System.setOut(out);
		}
	}
}
