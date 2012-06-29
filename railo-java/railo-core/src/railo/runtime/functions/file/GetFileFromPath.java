/**
 * Implements the CFML Function getfilefrompath
 */
package railo.runtime.functions.file;


import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class GetFileFromPath implements Function {
	public static String call(PageContext pc , String path) {
		path = path.replace('\\', '/');
        // get file name
		if(path.endsWith("..")) return ".";
        if(path.endsWith(".") || path.endsWith("/")) return "";
        return pc.getConfig().getResource(path).getName();
	}
}