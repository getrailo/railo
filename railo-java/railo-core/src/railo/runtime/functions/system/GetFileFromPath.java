/**
 * Implements the CFML Function getfilefrompath
 */
package railo.runtime.functions.system;


import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

/**
 * @deprecated replaced wih <code>#{@link railo.runtime.functions.file.GetFileFromPath}</code>
 */
public final class GetFileFromPath implements Function {
	public static String call(PageContext pc , String path) {
		return railo.runtime.functions.file.GetFileFromPath.call(pc, path);
	}
}