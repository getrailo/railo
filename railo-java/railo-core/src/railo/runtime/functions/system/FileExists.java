package railo.runtime.functions.system;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

/**
 * Implements the CFML Function fileexists
 * @deprecated replace with <code>railo.runtime.functions.file.FileExists</code>
 */
public final class FileExists implements Function {

	public static boolean call(PageContext pc , String string) throws PageException {
		return railo.runtime.functions.file.FileExists.call(pc, string);
	}
	
	public static boolean call(PageContext pc , String string,boolean allowRealPath) throws PageException {
		return railo.runtime.functions.file.FileExists.call(pc, string,allowRealPath);
	}
	
}