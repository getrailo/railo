package railo.commons.lang;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ExceptionUtil {
	
	public static String getStacktrace(Throwable t, boolean addMessage) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		pw.close();
		String st = sw.toString();
		String msg=t.getMessage();
		if(addMessage && !StringUtil.isEmpty(msg) && !st.startsWith(msg.trim()))
			st=msg+"\n"+st;
		return st;
		
	}
}
