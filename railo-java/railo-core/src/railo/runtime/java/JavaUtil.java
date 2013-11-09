package railo.runtime.java;


import java.net.URLDecoder;

import railo.commons.io.SystemUtil;
import railo.commons.lang.Charset;
import railo.commons.lang.ClassUtil;

public class JavaUtil {


	/**
	 * returns the path that the class was loaded from
	 *
	 * @param clazz
	 * @return
	 */
	public static String getJarPathForObject(Class clazz) {

		try {

			String result = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
			result = URLDecoder.decode(result, Charset.UTF8);
			result = SystemUtil.fixWindowsPath(result);
			return result;
		}
		catch (Throwable t) {}

		return "";
	}


	/**
	 * tries to load the class and returns that path it was loaded from
	 *
	 * @param className
	 * @return
	 */
	public static String getJarPathForClass(String className) {
		
		try {

			return  getJarPathForObject( ClassUtil.loadClass(className) );
		}
		catch (Throwable t) {}

		return "";
	}

}
