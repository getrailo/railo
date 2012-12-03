/**
 * Implements the CFML Function gettempdirectory
 */
package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class GetTempDirectory implements Function {
	public static String call(PageContext pc ) {
		String fs=System.getProperty("file.separator");
		String path= pc.getConfig().getTempDirectory().getAbsolutePath();
		
		if(path.lastIndexOf(fs)!=path.length()-1)
			path+=fs;
		return path;
	}
}