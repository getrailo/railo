package railo.runtime.java;


import railo.commons.io.SystemUtil;
import railo.commons.lang.Charset;
import railo.commons.lang.ClassException;
import railo.commons.lang.ClassUtil;

import java.net.URLDecoder;

public class JavaUtil {


	public static String getJarPathForClass(String className) {
		
		try {
			Class c = ClassUtil.loadClass(className);
			String result = c.getProtectionDomain().getCodeSource().getLocation().getPath();
			result = URLDecoder.decode(result, Charset.UTF8);
			result = SystemUtil.fixWindowsPath(result);
			return result;
		}
		catch (Throwable t) {}

		return "";
	}

}
