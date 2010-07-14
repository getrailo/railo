package railo.commons.lang;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ExceptionUtil {
	
	public static String getStacktrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		pw.close();
		return sw.toString();
	}
}
