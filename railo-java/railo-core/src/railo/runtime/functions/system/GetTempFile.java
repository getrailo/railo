/**
 * Implements the CFML Function gettempfile
 */
package railo.runtime.functions.system;

import java.io.IOException;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class GetTempFile implements Function {
	public static String call(PageContext pc , String strDir, String prefix) throws PageException {
	    Resource dir = ResourceUtil.toResourceExisting(pc, strDir);
        pc.getConfig().getSecurityManager().checkFileLocation(dir);
	    if(!dir.isDirectory()) throw new ExpressionException(strDir+" is not a directory");
	    int count=1;
	    Resource file;
	    while((file=dir.getRealResource(prefix+pc.getId()+count+".tmp")).exists()) {
	        count++;
	    }
	    try {
	    	file.createFile(false);
            //file.createNewFile();
    	    return file.getCanonicalPath();
        } 
	    catch (IOException e) {
            throw Caster.toPageException(e);
        }
	}
}