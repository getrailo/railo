package railo.runtime.java;


import railo.commons.io.SystemUtil;
import railo.commons.lang.Charset;

import java.net.URLDecoder;

public class JavaUtil {


	public static String getJarPathForClass(String className) {

		String result = "";

		try {

			Class c = Class.forName(className);

			result = c.getProtectionDomain().getCodeSource().getLocation().getPath();

			result = URLDecoder.decode(result, Charset.UTF8);

			result = SystemUtil.fixWindowsPath(result);
		}
		catch (Exception ex) {}

		return result;
	}

}
