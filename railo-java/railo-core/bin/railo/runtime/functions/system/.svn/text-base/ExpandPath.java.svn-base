/**
 * Implements the Cold Fusion Function expandpath
 */
package railo.runtime.functions.system;

import java.io.IOException;

import railo.commons.io.res.Resource;
import railo.commons.io.res.util.ResourceUtil;
import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.config.ConfigWebUtil;
import railo.runtime.exp.ExpressionException;
import railo.runtime.ext.function.Function;

public final class ExpandPath implements Function {
	public static String call(PageContext pc , String realPath) throws ExpressionException {
		
		Config config=pc.getConfig();
		realPath=realPath.replace('\\','/');
        Resource res;
        
        if(StringUtil.startsWith(realPath,'/')) {
        	res = pc.getPhysical(realPath,true);
            if(res!=null) {
            	return toReturnValue(realPath,res);
            }
        }
        realPath=ConfigWebUtil.replacePlaceholder(realPath, config);
        res=pc.getConfig().getResource(realPath);
        if(res.isAbsolute()) return toReturnValue(realPath,res);
        
        res=ResourceUtil.getResource(pc,pc.getBasePageSource());
        res = res.getParentResource().getRealResource(realPath);
        return toReturnValue(realPath,res);
        
	}

    private static String toReturnValue(String realPath,Resource res) {
        String path;
        char pathChar='/';
        try {
            path=res.getCanonicalPath();
            pathChar=ResourceUtil.FILE_SEPERATOR;
        } catch (IOException e) {
            path= res.getAbsolutePath();
        }
        boolean pathEndsWithSep=StringUtil.endsWith(path,pathChar);
        boolean realEndsWithSep=StringUtil.endsWith(realPath,'/');
        
        if(realEndsWithSep) {
            if(!pathEndsWithSep)path=path+pathChar;
        }
        else if(pathEndsWithSep) {
            path=path.substring(0,path.length()-1);
        }
        
        return path;
    }
}