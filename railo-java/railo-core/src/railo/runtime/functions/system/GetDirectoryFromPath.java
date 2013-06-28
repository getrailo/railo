/**
 * Implements the CFML Function getdirectoryfrompath
 */
package railo.runtime.functions.system;

import java.io.File;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;

public final class GetDirectoryFromPath implements Function {
	public static String call(PageContext pc , String path) {
		return invoke(path);
	}
	
	public static String invoke(String path) {
		int posOfLastDel = path.lastIndexOf('/');
		String parent = "";
		
		if(path.lastIndexOf('\\') > posOfLastDel)
			posOfLastDel = path.lastIndexOf("\\");
		if(posOfLastDel != -1)
			parent = path.substring(0, posOfLastDel + 1);
		else
		if(path.equals(".") || path.equals(".."))
			parent = String.valueOf(File.separatorChar);
		else if(path.startsWith("."))
			parent = String.valueOf(File.separatorChar);
		else
			parent = String.valueOf(File.separatorChar);
		return parent;
	}
}